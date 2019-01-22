package me.jiangcai.crud.row.field;

import me.jiangcai.crud.row.FieldDefinition;
import me.jiangcai.crud.row.field.fake.AbstractFake;
import me.jiangcai.crud.row.field.fake.FakeRoot;
import org.springframework.http.MediaType;

import javax.persistence.criteria.*;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 基本字段定义，仅根据表达式(Expression)
 *
 * @param <T> 它可以处理的root的类型
 * @author CJ
 */
public class BasicExpressionField<T> implements FieldDefinition<T> {

    private final Function<Root<T>, Expression<?>> toExpression;
    private final BiFunction<Root<T>, CriteriaBuilder, Expression<?>> toExpression2;
    private final String name;
    private final Function<T, ?> entityFunction;

    BasicExpressionField(String name, Function<Root<T>, Expression<?>> toExpression) {
        this.name = name;
        this.toExpression = toExpression;
        this.toExpression2 = null;
        this.entityFunction = null;
    }

    BasicExpressionField(String name, BiFunction<Root<T>, CriteriaBuilder, Expression<?>> toExpression) {
        this(name, toExpression, null);
    }

    BasicExpressionField(String name, BiFunction<Root<T>, CriteriaBuilder, Expression<?>> toExpression
            , Function<T, ?> entityFunction) {
        this.name = name;
        this.toExpression = null;
        this.toExpression2 = toExpression;
        this.entityFunction = entityFunction;
    }

    @Override
    public Selection<?> select(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query, Root<T> root) {
        return innerExpression(criteriaBuilder, root);
    }

    private Expression<?> innerExpression(CriteriaBuilder criteriaBuilder, Root<T> root) {
        if (toExpression2 != null)
            return toExpression2.apply(root, criteriaBuilder);
        if (toExpression != null)
            return toExpression.apply(root);
        throw new IllegalStateException("没有表达式的BasicExpressionField");
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Object export(Object origin, MediaType mediaType, Function<List, ?> exportMe) {
        return origin;
    }

    @Override
    public Expression<?> order(CriteriaQuery query, CriteriaBuilder criteriaBuilder, Root<T> root) {
        return innerExpression(criteriaBuilder, root);
    }

    @Override
    public Object readValue(T entity) {
        if (entityFunction != null)
            return entityFunction.apply(entity);
        if (toExpression != null) {
            AbstractFake fake = (AbstractFake) toExpression.apply(new FakeRoot<>());
            return fake.toValue(entity);
        }
        if (toExpression2 != null)
            throw new IllegalStateException("暂不支持携带有CriteriaBuilder的完整取值。");

        throw new NoSuchMethodError("readValue");
    }
}
