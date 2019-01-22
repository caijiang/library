package me.jiangcai.crud.row.field.fake;

import javax.persistence.criteria.CollectionJoin;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.CollectionAttribute;
import java.util.Collection;

/**
 * @author CJ
 */
public class FakeCollectionJoin<Z, E>
        extends FakePluralJoin<Z, Collection<E>, E> implements CollectionJoin<Z, E> {
    public FakeCollectionJoin(AbstractFake fake) {
        super(fake);
    }

    public FakeCollectionJoin() {
        super();
    }

    @SuppressWarnings("unchecked")
    @Override
    public CollectionAttribute<? super Z, E> getModel() {
        return (CollectionAttribute<? super Z, E>) super.getModel();
    }

    @Override
    public CollectionJoin<Z, E> on(Expression<Boolean> restriction) {
        return (CollectionJoin<Z, E>) super.on(restriction);
    }

    @Override
    public CollectionJoin<Z, E> on(Predicate... restrictions) {
        return (CollectionJoin<Z, E>) super.on(restrictions);
    }
}
