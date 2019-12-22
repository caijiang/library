package me.jiangcai.crud.row.bean;

import kotlin.Pair;
import lombok.AllArgsConstructor;
import me.jiangcai.crud.modify.PropertyChanger;
import me.jiangcai.crud.row.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

/**
 * @author CJ
 */
@Service
public class RowServiceImpl implements RowService {

    private static final Log log = LogFactory.getLog(RowServiceImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    private final List<PropertyChanger> changerSet;

    @Override
    public <T> List<T> queryAllEntity(RowDefinition<T> definition) {
        definition.fields();
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(definition.entityClass());
        Root<T> root = cq.from(definition.entityClass());

        final Specification<T> specification = definition.specification();
        if (specification != null) {
            cq = cq.where(specification.toPredicate(root, cq, cb));
        }

        final List<Order> o = definition.defaultOrder(cb, root);
        if (o != null)
            cq = cq.orderBy(o);

        return entityManager.createQuery(cq).getResultList();
    }

    public RowServiceImpl(List<PropertyChanger> changerSet) {
        this.changerSet = changerSet;
    }

    @Override
    public <T> Page<T> queryEntity(RowDefinition<T> definition, Pageable pageable) {
        definition.fields();
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(definition.entityClass());
        Root<T> root = cq.from(definition.entityClass());

        final Specification<T> specification = definition.specification();
        if (specification != null) {
            cq = cq.where(specification.toPredicate(root, cq, cb));
        }

        final List<Order> o = definition.defaultOrder(cb, root);
        if (o != null)
            cq = cq.orderBy(o);

        final List<T> resultList = entityManager.createQuery(cq).setMaxResults(pageable.getPageSize()).setFirstResult(pageable.getOffset()).getResultList();
        // and the total
        CriteriaQuery<Long> countCq = cb.createQuery(Long.class);
        Root<T> countRoot = countCq.from(definition.entityClass());
        if (specification != null) {
            countCq = countCq.where(specification.toPredicate(countRoot, countCq, cb));
        }
        countCq = countCq.select(cb.count(countRoot));
        return new PageImpl<>(resultList, pageable, entityManager.createQuery(countCq).getSingleResult());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Pair<List<?>, Map<String, ?>> queryFields(RowDefinition rowDefinition, boolean distinct
            , OrderGenerator customOrderFunction) {
        final List<FieldDefinition> fieldDefinitions = rowDefinition.fields();

        QueryPair resultPair = smartQuery(rowDefinition, distinct, customOrderFunction, fieldDefinitions, null);
        try {
            final List originList = entityManager.createQuery(resultPair.dataQuery).getResultList();
            return new Pair(originList.stream().map(this::fromTuple).collect(Collectors.toList()), null);
        } catch (NoResultException ex) {
            log.debug("RW Result: no result found.");
            return new Pair(Collections.emptyList(), null);
        }
    }

    private Object fromTuple(Object origin) {
        if (origin instanceof Tuple) {
            return ((Tuple) origin).toArray();
        }
        return origin;
    }

    @Override
    public Page<?> queryFields(RowDefinition rowDefinition, boolean distinct, OrderGenerator customOrderFunction
            , Pageable pageable) {
        return queryFields(rowDefinition, distinct, customOrderFunction, pageable, null).getFirst();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Pair<Page<?>, Map<String, ?>> queryFields(RowDefinition rowDefinition, boolean distinct,
                                                     OrderGenerator customOrderFunction, Pageable pageable, List<Pair<String, List<String>>> filters) {
        final List<FieldDefinition> fieldDefinitions = rowDefinition.fields();

        QueryPair resultPair = smartQuery(rowDefinition, distinct, customOrderFunction, fieldDefinitions, filters);

        // 打包成Object[]
        try {
            long total;
            try {
                total = entityManager.createQuery(resultPair.countQuery).getSingleResult();
            } catch (NonUniqueResultException ex) {
                total = entityManager.createQuery(resultPair.countQuery).getResultList().size();
            }
            List<?> list;
            if (total == 0)
                list = Collections.emptyList();
            else {
                list = entityManager.createQuery(resultPair.dataQuery)
                        .setFirstResult(pageable.getOffset())
                        .setMaxResults(pageable.getPageSize())
                        .getResultList();
            }

            // 输出到结果
            log.debug("RW Result: total:" + total + ", list:" + list + ", fields:" + fieldDefinitions.size());
            return new Pair(new PageImpl(list.stream().map(this::fromTuple).collect(Collectors.toList()), pageable, total), readOtherAsMap(resultPair.otherQuery));
        } catch (NoResultException ex) {
            log.debug("RW Result: no result found.");
            return new Pair(new PageImpl(Collections.EMPTY_LIST, pageable, 0), new HashMap<String, Object>());
        }
    }

    private Map<String, Object> readOtherAsMap(CriteriaQuery<Tuple> query) {
        if (query == null)
            return null;
        try {
            Tuple tuple = entityManager.createQuery(query).getSingleResult();
            Map<String, Object> x = new HashMap<>();
            tuple.getElements().forEach(it -> x.put(it.getAlias(), tuple.get(it.getAlias())));
            return x;
        } catch (NoResultException ex) {
            return null;
        } catch (NonUniqueResultException ex) {
            throw new IllegalStateException("需要聚合结果，结果聚合结果给出了多个！");
        }
    }

    @SuppressWarnings("unchecked")
    private QueryPair smartQuery(RowDefinition rowDefinition, boolean distinct
            , OrderGenerator customOrderFunction
            , List<FieldDefinition> fieldDefinitions, List<Pair<String, List<String>>> filters) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        // 在这里勾画出它的类型
        // 装饰器的任务则是读取他们的过滤器
        // 过滤器跟本身既有的filed 进行组合，组合出可行的过滤器。
        final Specification filterSpecification;
        if (filters != null && !filters.isEmpty()) {
//                组合
            Map<String, FieldDefinition> mappedDefinitions = new HashMap<>();
            Set<String> names = filters.stream()
                    .map(Pair::getFirst)
                    .collect(Collectors.toSet());

            fieldDefinitions.forEach(fieldDefinition -> {
                if (names.contains(fieldDefinition.name())) {
                    mappedDefinitions.put(fieldDefinition.name(), fieldDefinition);
                }
            });

            List<Pair<TypeFieldDefinition, Set<Object>>> fs = filters.stream()
                    // 这次过滤 留下来就是精英了。
                    .filter(stringListPair -> {
                        if (!mappedDefinitions.containsKey(stringListPair.getFirst())) {
                            log.debug("声明了filter 名称" + stringListPair.getFirst() + ",但是映射定义中并未包含。");
                            return false;
                        }

                        final FieldDefinition definition = mappedDefinitions.get(stringListPair.getFirst());
                        if (log.isDebugEnabled() && !(definition instanceof TypeFieldDefinition)) {
                            log.debug("filter:" + stringListPair.getFirst() + " 所对应的映射定义并未实现TypeFieldDefinition");
                        }
                        return definition instanceof TypeFieldDefinition;
                    })
                    .map((Function<Pair<String, List<String>>, Pair<TypeFieldDefinition, Set<Object>>>) stringListPair -> {
                        TypeFieldDefinition def = (TypeFieldDefinition) mappedDefinitions.get(stringListPair.getFirst());

                        return new Pair(def, stringListPair.getSecond()
                                .stream()
                                .map(s -> changerSet.stream()
                                        .filter(propertyChanger -> propertyChanger.support(def.getResultType()))
                                        .findFirst()
                                        .orElseThrow(() -> new IllegalStateException("没有找到合适的转换器(PropertyChanger)转换到" + def.getResultType()))
                                        .change(def.getResultType(), s))
                                .collect(Collectors.toSet()));
                    })
                    .collect(Collectors.toList());

            if (fs.isEmpty()) {
                filterSpecification = null;
            } else {
                filterSpecification = (root, query, cb) -> cb.and(fs.stream()
                        .map(typeFieldDefinitionSetPair -> {
                            Expression selection = typeFieldDefinitionSetPair.getFirst().select(cb, query, root);

                            return cb.or(typeFieldDefinitionSetPair.getSecond().stream()
                                    .map(o -> cb.equal(selection, o)).toArray(Predicate[]::new));
                        })
                        .toArray(Predicate[]::new));
            }
        } else {
            filterSpecification = null;
        }
//        CriteriaQuery originDataQuery = criteriaBuilder.createQuery();
//        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        QueryPair pair = new QueryPair(criteriaBuilder);
//        Subquery subquery = null;
//        subquery.from(rowDefinition.entityClass());

        Root root = pair.dataQuery.from(rowDefinition.entityClass());
        Root countRoot = pair.countQuery.from(rowDefinition.entityClass());
        Root otherRoot = pair.otherQuery.from(rowDefinition.entityClass());

        CriteriaQuery dataQuery = pair.dataQuery.select(criteriaBuilder.tuple(
                fieldDefinitions.stream()
                        .map(fieldDefinition -> {
                            return fieldDefinition.select(criteriaBuilder, pair.dataQuery, root);
                        })
                        .filter(Objects::nonNull)
                        .toArray((IntFunction<Selection<?>[]>) Selection[]::new)
        ));
//        @SuppressWarnings("Convert2Lambda")
//        CriteriaQuery dataQuery = pair.dataQuery.multiselect(fieldDefinitions.stream()
//                .map(new Function<FieldDefinition, Selection>() {
//                    @Override
//                    public Selection apply(FieldDefinition fieldDefinition) {
////                        field
////                                -> field.select(criteriaBuilder, originDataQuery, root)
//                        return fieldDefinition.select(criteriaBuilder, pair.dataQuery, root);
//                    }
//                })
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList()));

        // where
        dataQuery = where(criteriaBuilder, dataQuery, root, rowDefinition, filterSpecification);
        dataQuery = rowDefinition.dataGroup(criteriaBuilder, dataQuery, root);
        pair.countQuery = where(criteriaBuilder, pair.countQuery, countRoot, rowDefinition, filterSpecification);
        pair.countQuery = rowDefinition.countQuery(criteriaBuilder, pair.countQuery, countRoot);
        pair.otherQuery = rowDefinition.sampleQuery(criteriaBuilder
                , pair.otherQuery
                , otherRoot);

        if (pair.otherQuery != null) {
            pair.otherQuery = where(criteriaBuilder, pair.otherQuery, root, rowDefinition, filterSpecification);
        }

        if (distinct)
            pair.countQuery = pair.countQuery.select(criteriaBuilder.countDistinct(rowDefinition.count(pair.countQuery, criteriaBuilder, countRoot)));
        else
            pair.countQuery = pair.countQuery.select(criteriaBuilder.count(rowDefinition.count(pair.countQuery, criteriaBuilder, countRoot)));

        // Distinct
        if (distinct)
            dataQuery = dataQuery.distinct(true);

        // sort
        List<Order> order = customOrderFunction == null ? null : customOrderFunction.toOrder(pair.countQuery, criteriaBuilder, root);
        if (CollectionUtils.isEmpty(order))
            order = rowDefinition.defaultOrder(criteriaBuilder, root);

        if (!CollectionUtils.isEmpty(order))
            dataQuery = dataQuery.orderBy(order);


        return new QueryPair(dataQuery, pair.countQuery, pair.otherQuery);
    }

    private <T> CriteriaQuery<T> where(CriteriaBuilder criteriaBuilder, CriteriaQuery<T> query, Root<?> root
            , RowDefinition<?> rowDefinition, Specification filterSpecification) {
        final Specification<?> specification = rowDefinition.specification();
        if (specification == null) {
            if (filterSpecification == null)
                return query;
            //noinspection unchecked
            return query.where(filterSpecification.toPredicate(root, query, criteriaBuilder));
        }
        if (filterSpecification == null)
            //noinspection unchecked
            return query.where(specification.toPredicate((Root) root, query, criteriaBuilder));

        //noinspection unchecked
        return query.where(Specifications.where(specification).and(filterSpecification).toPredicate(root, query, criteriaBuilder));
    }

    @AllArgsConstructor
    private class QueryPair {
        CriteriaQuery<Tuple> dataQuery;
        CriteriaQuery<Long> countQuery;
        CriteriaQuery<Tuple> otherQuery;

        QueryPair(CriteriaBuilder criteriaBuilder) {
            dataQuery = criteriaBuilder.createTupleQuery();
            countQuery = criteriaBuilder.createQuery(Long.class);
            otherQuery = criteriaBuilder.createTupleQuery();
        }
    }
}
