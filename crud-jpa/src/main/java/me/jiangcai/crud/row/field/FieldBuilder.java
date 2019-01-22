package me.jiangcai.crud.row.field;

import me.jiangcai.crud.row.FieldDefinition;
import me.jiangcai.crud.row.field.fake.AbstractFake;
import me.jiangcai.crud.row.field.fake.FakeRoot;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;

import javax.persistence.criteria.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 字段构建器
 *
 * @author CJ
 */
public class FieldBuilder<T> {

    private final String name;
    private BiFunction<Root<T>, CriteriaBuilder, Expression<?>> biSelect;
    private OwnExpression<T> ownSelect;
    private Function<Root<T>, Expression<?>> select;
    private BiFunction<Object, MediaType, Object> format;
    private Function<Root<T>, Expression<?>> order;
    private BiFunction<Root<T>, CriteriaBuilder, Expression<?>> biOrder;
    private Function<T, ?> entityFunction;
    private boolean noOrder = false;
    private OwnExpression<T> ownOrder;

    private FieldBuilder(String name) {
        this.name = name;
    }

    /**
     * 开始一个构造器
     *
     * @param name {@link FieldDefinition#name()}
     * @param <Y>  动态类型
     * @return 新的构造器
     */
    public static <Y> FieldBuilder<Y> asName(Class<Y> type, String name) {
        return new FieldBuilder<>(name);
    }

    /**
     * 开始一个构造器
     *
     * @param name {@link FieldDefinition#name()}
     * @return 新的构造器
     */
    public static <X> FieldBuilder<X> asName(String name) {
        return new FieldBuilder<X>(name);
    }

    /**
     * 获取数据若是需要CriteriaBuilder的配合
     *
     * @param function 支持传入 CriteriaBuilder的定制函数
     * @return this
     */
    public FieldBuilder<T> addBiSelect(BiFunction<Root<T>, CriteriaBuilder, Expression<?>> function) {
        this.biSelect = function;
        return this;
    }

    /**
     * 获取数据时若需要当时的查询配合
     *
     * @param ownSelect 完全定制的函数
     * @return this
     */
    public FieldBuilder<T> addOwnSelect(OwnExpression<T> ownSelect) {
        this.ownSelect = ownSelect;
        return this;
    }

    /**
     * 获取数据仅需Root
     *
     * @param function root为参数的函数
     * @return this
     */
    public FieldBuilder<T> addSelect(Function<Root<T>, Expression<?>> function) {
        this.select = function;
        return this;
    }

    public FieldBuilder<T> addOwnOrder(OwnExpression<T> ownOrder) {
        this.ownOrder = ownOrder;
        return this;
    }

    public FieldBuilder<T> addBiOrder(BiFunction<Root<T>, CriteriaBuilder, Expression<?>> function) {
        this.biOrder = function;
        return this;
    }

    public FieldBuilder<T> addOrder(Function<Root<T>, Expression<?>> function) {
        this.order = function;
        return this;
    }

    public FieldBuilder<T> withoutOrder() {
        this.noOrder = true;
        return this;
    }

    /**
     * 从实体中获取的数据并不符合数据规格，则可调用该方法
     *
     * @param format 将原数据和期望Media转变为新数据
     * @return this
     */
    public FieldBuilder<T> addFormat(BiFunction<Object, MediaType, Object> format) {
        this.format = format;
        return this;
    }

    /**
     * 如果采用了{@link #addBiSelect(BiFunction)}或者{@link #addOwnSelect(OwnExpression)}会导致生成的FieldDefinition的
     * {@link FieldDefinition#readValue(Object)}无法正常工作，这个时候需要调用该方法实施定制
     *
     * @param entityFunction 获取值的办法
     * @return this
     */
    public FieldBuilder<T> addEntityFunction(Function<T, ?> entityFunction) {
        this.entityFunction = entityFunction;
        return this;
    }

    public FieldDefinition<T> build() {
        return new FieldDefinition<T>() {
            @Override
            public Object readValue(T entity) {
                if (entityFunction != null)
                    return entityFunction.apply(entity);
                if (select != null) {
                    AbstractFake fake = (AbstractFake) select.apply(new FakeRoot<>());
                    return fake.toValue(entity);
                }
                try {
                    return BeanUtils.getPropertyDescriptor(entity.getClass(), name()).getReadMethod().invoke(entity);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new IllegalStateException(e);
                }
            }

            @Override
            public Selection<?> select(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query, Root<T> root) {
                if (ownSelect != null)
                    return ownSelect.toExpression(root, criteriaBuilder, query);
                if (biSelect != null)
                    return biSelect.apply(root, criteriaBuilder);
                if (select != null)
                    return select.apply(root);
                return root.get(name);
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public Object export(Object origin, MediaType mediaType, Function<List, ?> exportMe) {
                if (format != null)
                    return format.apply(origin, mediaType);
                return origin;
            }

            @Override
            public Expression<?> order(CriteriaQuery query, CriteriaBuilder criteriaBuilder, Root<T> root) {
                if (noOrder)
                    return null;
                if (ownOrder != null)
                    return ownOrder.toExpression(root, criteriaBuilder, query);
                if (biOrder != null)
                    return biOrder.apply(root, criteriaBuilder);
                if (order != null)
                    return order.apply(root);
                if (ownSelect != null)
                    return ownSelect.toExpression(root, criteriaBuilder, query);
                if (biSelect != null)
                    return biSelect.apply(root, criteriaBuilder);
                if (select != null)
                    return select.apply(root);
                return root.get(name);
            }
        };
    }

    @FunctionalInterface
    public interface OwnExpression<T> {
        Expression<?> toExpression(Root<T> root, CriteriaBuilder cb, CriteriaQuery<?> query);
    }


}
