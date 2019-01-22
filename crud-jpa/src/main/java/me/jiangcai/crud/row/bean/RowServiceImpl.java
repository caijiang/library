package me.jiangcai.crud.row.bean;

import lombok.AllArgsConstructor;
import me.jiangcai.crud.row.FieldDefinition;
import me.jiangcai.crud.row.OrderGenerator;
import me.jiangcai.crud.row.RowDefinition;
import me.jiangcai.crud.row.RowService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.criteria.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author CJ
 */
@Service
public class RowServiceImpl implements RowService {

    private static final Log log = LogFactory.getLog(RowServiceImpl.class);

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;

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
        return new PageImpl<T>(resultList, pageable, entityManager.createQuery(countCq).getSingleResult());
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<?> queryFields(RowDefinition rowDefinition, boolean distinct
            , OrderGenerator customOrderFunction) {
        final List<FieldDefinition> fieldDefinitions = rowDefinition.fields();

        QueryPair resultPair = smartQuery(rowDefinition, distinct, customOrderFunction, fieldDefinitions);
        try {
            return entityManager.createQuery(resultPair.dataQuery).getResultList();
        } catch (NoResultException ex) {
            log.debug("RW Result: no result found.");
            return Collections.emptyList();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Page<?> queryFields(RowDefinition rowDefinition, boolean distinct,
                               OrderGenerator customOrderFunction, Pageable pageable) {
        final List<FieldDefinition> fieldDefinitions = rowDefinition.fields();

        QueryPair resultPair = smartQuery(rowDefinition, distinct, customOrderFunction, fieldDefinitions);

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
            return new PageImpl(list, pageable, total);
        } catch (NoResultException ex) {
            log.debug("RW Result: no result found.");
            return new PageImpl(Collections.EMPTY_LIST, pageable, 0);
        }
    }

    @SuppressWarnings("unchecked")
    private QueryPair smartQuery(RowDefinition rowDefinition, boolean distinct
            , OrderGenerator customOrderFunction
            , List<FieldDefinition> fieldDefinitions) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//        CriteriaQuery originDataQuery = criteriaBuilder.createQuery();
//        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        QueryPair pair = new QueryPair(criteriaBuilder);
//        Subquery subquery = null;
//        subquery.from(rowDefinition.entityClass());

        Root root = pair.dataQuery.from(rowDefinition.entityClass());
        Root countRoot = pair.countQuery.from(rowDefinition.entityClass());


        CriteriaQuery dataQuery = pair.dataQuery.multiselect(fieldDefinitions.stream()
                .map(new Function<FieldDefinition, Selection>() {
                    @Override
                    public Selection apply(FieldDefinition fieldDefinition) {
//                        field
//                                -> field.select(criteriaBuilder, originDataQuery, root)
                        return fieldDefinition.select(criteriaBuilder, pair.dataQuery, root);
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));

        // where
        dataQuery = where(criteriaBuilder, dataQuery, root, rowDefinition);
        dataQuery = rowDefinition.dataGroup(criteriaBuilder, dataQuery, root);
        pair.countQuery = where(criteriaBuilder, pair.countQuery, countRoot, rowDefinition);
        pair.countQuery = rowDefinition.countQuery(criteriaBuilder, pair.countQuery, countRoot);

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


        return new QueryPair(dataQuery, pair.countQuery);
    }

    private <T> CriteriaQuery<T> where(CriteriaBuilder criteriaBuilder, CriteriaQuery<T> query, Root<?> root
            , RowDefinition<?> rowDefinition) {
        final Specification<?> specification = rowDefinition.specification();
        if (specification == null)
            return query;
        //noinspection unchecked
        return query.where(specification.toPredicate((Root) root, query, criteriaBuilder));
    }

    @AllArgsConstructor
    private class QueryPair {
        CriteriaQuery dataQuery;
        CriteriaQuery<Long> countQuery;

        QueryPair(CriteriaBuilder criteriaBuilder) {
            dataQuery = criteriaBuilder.createQuery();
            countQuery = criteriaBuilder.createQuery(Long.class);
        }
    }
}
