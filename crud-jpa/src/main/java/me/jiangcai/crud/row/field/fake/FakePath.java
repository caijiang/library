package me.jiangcai.crud.row.field.fake;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import java.util.Collection;
import java.util.Map;

/**
 * @author CJ
 */
public class FakePath<T> extends FakeExpression<T> implements Path<T> {
    public FakePath(AbstractFake fake) {
        super(fake);
    }

    public FakePath() {
        super();
    }

    @Override
    public Bindable<T> getModel() {
        throw new NoSuchMethodError("");
    }

    @Override
    public Path<?> getParentPath() {
        throw new NoSuchMethodError("");
    }

    @Override
    public <Y> Path<Y> get(SingularAttribute<? super T, Y> attribute) {
        FakePath<Y> path = new FakePath<>(this);
        path.andThenForName(attribute.getName());
        return path;
    }

    @Override
    public <E, C extends Collection<E>> Expression<C> get(PluralAttribute<T, C, E> collection) {
        FakeExpression<C> expression = new FakeExpression<>(this);
        expression.andThenForName(collection.getName());
        return expression;
    }

    @Override
    public <K, V, M extends Map<K, V>> Expression<M> get(MapAttribute<T, K, V> map) {
        FakeExpression<M> expression = new FakeExpression<>(this);
        expression.andThenForName(map.getName());
        return expression;
    }

    @Override
    public Expression<Class<? extends T>> type() {
        FakeExpression<Class<? extends T>> expression = new FakeExpression<>(this);
        expression.andThen(Object::getClass);
        return expression;
    }

    @Override
    public <Y> Path<Y> get(String attributeName) {
        FakePath<Y> path = new FakePath<>(this);
        path.andThenForName(attributeName);
        return path;
    }
}
