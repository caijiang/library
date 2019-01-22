package me.jiangcai.crud.row.field.fake;

import javax.persistence.criteria.*;
import javax.persistence.metamodel.*;
import java.util.Set;

/**
 * @author CJ
 */
public class FakeFrom<Z, T> extends FakePath<T> implements From<Z, T> {
    public FakeFrom(AbstractFake fake) {
        super(fake);
    }

    public FakeFrom() {
        super();
    }

    @Override
    public Set<Join<T, ?>> getJoins() {
        throw new NoSuchMethodError("");
    }

    @Override
    public boolean isCorrelated() {
        throw new NoSuchMethodError("");
    }

    @Override
    public From<Z, T> getCorrelationParent() {
        throw new NoSuchMethodError("");
    }

    @Override
    public <Y> Join<T, Y> join(SingularAttribute<? super T, Y> attribute) {
        FakeJoin<T, Y> join = new FakeJoin<>(this);
        join.andThenForName(attribute.getName());
        return join;
    }

    @Override
    public <Y> Join<T, Y> join(SingularAttribute<? super T, Y> attribute, JoinType jt) {
        FakeJoin<T, Y> join = new FakeJoin<>(this);
        join.andThenForName(attribute.getName());
        if (jt != null && jt == JoinType.LEFT)
            join.leftJoin();
        return join;
    }

    @Override
    public <Y> CollectionJoin<T, Y> join(CollectionAttribute<? super T, Y> collection) {
        FakeCollectionJoin<T, Y> join = new FakeCollectionJoin<>(this);
        join.andThenForName(collection.getName());
        return join;
    }

    @Override
    public <Y> SetJoin<T, Y> join(SetAttribute<? super T, Y> set) {
        FakeSetJoin<T, Y> join = new FakeSetJoin<>(this);
        join.andThenForName(set.getName());
        return join;
    }

    @Override
    public <Y> ListJoin<T, Y> join(ListAttribute<? super T, Y> list) {
        FakeListJoin<T, Y> join = new FakeListJoin<>(this);
        join.andThenForName(list.getName());
        return join;
    }

    @Override
    public <K, V> MapJoin<T, K, V> join(MapAttribute<? super T, K, V> map) {
        return null;
    }

    @Override
    public <Y> CollectionJoin<T, Y> join(CollectionAttribute<? super T, Y> collection, JoinType jt) {
        FakeCollectionJoin<T, Y> join = new FakeCollectionJoin<>(this);
        join.andThenForName(collection.getName());
        if (jt != null && jt == JoinType.LEFT)
            join.leftJoin();
        return join;
    }

    @Override
    public <Y> SetJoin<T, Y> join(SetAttribute<? super T, Y> set, JoinType jt) {
        FakeSetJoin<T, Y> join = new FakeSetJoin<>(this);
        join.andThenForName(set.getName());
        if (jt != null && jt == JoinType.LEFT)
            join.leftJoin();
        return join;
    }

    @Override
    public <Y> ListJoin<T, Y> join(ListAttribute<? super T, Y> list, JoinType jt) {
        FakeListJoin<T, Y> join = new FakeListJoin<>(this);
        join.andThenForName(list.getName());
        if (jt != null && jt == JoinType.LEFT)
            join.leftJoin();
        return join;
    }

    @Override
    public <K, V> MapJoin<T, K, V> join(MapAttribute<? super T, K, V> map, JoinType jt) {
        FakeMapJoin<T, K, V> join = new FakeMapJoin<>(this);
        join.andThenForName(map.getName());
        if (jt != null && jt == JoinType.LEFT)
            join.leftJoin();
        return join;
    }

    @Override
    public <X, Y> Join<X, Y> join(String attributeName) {
        FakeJoin<X, Y> join = new FakeJoin<>(this);
        join.andThenForName(attributeName);
        return join;
    }

    @Override
    public <X, Y> CollectionJoin<X, Y> joinCollection(String attributeName) {
        FakeCollectionJoin<X, Y> join = new FakeCollectionJoin<>(this);
        join.andThenForName(attributeName);
        return join;
    }

    @Override
    public <X, Y> SetJoin<X, Y> joinSet(String attributeName) {
        FakeSetJoin<X, Y> join = new FakeSetJoin<>(this);
        join.andThenForName(attributeName);
        return join;
    }

    @Override
    public <X, Y> ListJoin<X, Y> joinList(String attributeName) {
        FakeListJoin<X, Y> join = new FakeListJoin<>(this);
        join.andThenForName(attributeName);
        return join;
    }

    @Override
    public <X, K, V> MapJoin<X, K, V> joinMap(String attributeName) {
        FakeMapJoin<X, K, V> join = new FakeMapJoin<>(this);
        join.andThenForName(attributeName);
        return join;
    }

    @Override
    public <X, Y> Join<X, Y> join(String attributeName, JoinType jt) {
        FakeJoin<X, Y> join = new FakeJoin<>(this);
        join.andThenForName(attributeName);
        if (jt != null && jt == JoinType.LEFT)
            join.leftJoin();
        return join;
    }

    @Override
    public <X, Y> CollectionJoin<X, Y> joinCollection(String attributeName, JoinType jt) {
        FakeCollectionJoin<X, Y> join = new FakeCollectionJoin<>(this);
        join.andThenForName(attributeName);
        if (jt != null && jt == JoinType.LEFT)
            join.leftJoin();
        return join;
    }

    @Override
    public <X, Y> SetJoin<X, Y> joinSet(String attributeName, JoinType jt) {
        FakeSetJoin<X, Y> join = new FakeSetJoin<>(this);
        join.andThenForName(attributeName);
        if (jt != null && jt == JoinType.LEFT)
            join.leftJoin();
        return join;
    }

    @Override
    public <X, Y> ListJoin<X, Y> joinList(String attributeName, JoinType jt) {
        FakeListJoin<X, Y> join = new FakeListJoin<>(this);
        join.andThenForName(attributeName);
        if (jt != null && jt == JoinType.LEFT)
            join.leftJoin();
        return join;
    }

    @Override
    public <X, K, V> MapJoin<X, K, V> joinMap(String attributeName, JoinType jt) {
        FakeMapJoin<X, K, V> join = new FakeMapJoin<>(this);
        join.andThenForName(attributeName);
        if (jt != null && jt == JoinType.LEFT)
            join.leftJoin();
        return join;
    }

    @Override
    public Set<Fetch<T, ?>> getFetches() {
        throw new NoSuchMethodError("");
    }

    @Override
    public <Y> Fetch<T, Y> fetch(SingularAttribute<? super T, Y> attribute) {
        throw new NoSuchMethodError("");
    }

    @Override
    public <Y> Fetch<T, Y> fetch(SingularAttribute<? super T, Y> attribute, JoinType jt) {
        throw new NoSuchMethodError("");
    }

    @Override
    public <Y> Fetch<T, Y> fetch(PluralAttribute<? super T, ?, Y> attribute) {
        throw new NoSuchMethodError("");
    }

    @Override
    public <Y> Fetch<T, Y> fetch(PluralAttribute<? super T, ?, Y> attribute, JoinType jt) {
        throw new NoSuchMethodError("");
    }

    @Override
    public <X, Y> Fetch<X, Y> fetch(String attributeName) {
        throw new NoSuchMethodError("");
    }

    @Override
    public <X, Y> Fetch<X, Y> fetch(String attributeName, JoinType jt) {
        throw new NoSuchMethodError("");
    }
}
