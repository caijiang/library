package me.jiangcai.crud.row.field.fake;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.MapAttribute;
import java.util.Map;

/**
 * @author CJ
 */
public class FakeMapJoin<Z, K, V>
        extends FakePluralJoin<Z, Map<K, V>, V> implements MapJoin<Z, K, V> {
    public FakeMapJoin(AbstractFake fake) {
        super(fake);
    }

    public FakeMapJoin() {
        super();
    }

    @SuppressWarnings("unchecked")
    @Override
    public MapAttribute<? super Z, K, V> getModel() {
        return (MapAttribute<? super Z, K, V>) super.getModel();
    }

    @Override
    public Path<K> key() {
        throw new NoSuchMethodError("index 到底是什么意思？");
    }

    @Override
    public Path<V> value() {
        throw new NoSuchMethodError("index 到底是什么意思？");
    }

    @Override
    public Expression<Map.Entry<K, V>> entry() {
        throw new NoSuchMethodError("index 到底是什么意思？");
    }

    @Override
    public MapJoin<Z, K, V> on(Expression<Boolean> restriction) {
        return (MapJoin<Z, K, V>) super.on(restriction);
    }

    @Override
    public MapJoin<Z, K, V> on(Predicate... restrictions) {
        return (MapJoin<Z, K, V>) super.on(restrictions);
    }
}
