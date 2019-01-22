package me.jiangcai.crud.row.field.fake;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Selection;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author CJ
 */
public class FakeExpression<T> extends AbstractFake implements Expression<T> {

    public FakeExpression(AbstractFake fake) {
        super(fake);
    }

    public FakeExpression() {
    }

    @Override
    public Predicate isNull() {
        FakePredicate predicate = new FakePredicate(this);
        predicate.andThen(Objects::isNull);
        return predicate;
    }

    @Override
    public Predicate isNotNull() {
        FakePredicate predicate = new FakePredicate(this);
        predicate.andThen(Objects::nonNull);
        return predicate;
    }

    @Override
    public Predicate in(Object... values) {
        throw new NoSuchMethodError("");
    }

    @Override
    public <X> Expression<X> as(Class<X> type) {
        //noinspection unchecked
        return (Expression<X>) this;
    }


    @Override
    public Predicate in(Expression values) {
        throw new NoSuchMethodError("");
    }

    @Override
    public Predicate in(Collection values) {
        throw new NoSuchMethodError("");
    }

    @Override
    public Predicate in(Expression[] values) {
        throw new NoSuchMethodError("");
    }

    @Override
    public Selection<T> alias(String name) {
        return this;
    }

    @Override
    public boolean isCompoundSelection() {
        throw new NoSuchMethodError("");
    }

    @Override
    public List<Selection<?>> getCompoundSelectionItems() {
        throw new NoSuchMethodError("");
    }

    @Override
    public Class<? extends T> getJavaType() {
        throw new NoSuchMethodError("");
    }

    @Override
    public String getAlias() {
        throw new NoSuchMethodError("");
    }
}
