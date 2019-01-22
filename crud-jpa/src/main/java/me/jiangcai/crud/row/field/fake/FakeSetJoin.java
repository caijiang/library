package me.jiangcai.crud.row.field.fake;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.SetJoin;
import javax.persistence.metamodel.SetAttribute;
import java.util.Set;

/**
 * @author CJ
 */
public class FakeSetJoin<Z, E> extends FakePluralJoin<Z, Set<E>, E> implements SetJoin<Z, E> {
    public FakeSetJoin(AbstractFake fake) {
        super(fake);
    }

    public FakeSetJoin() {
        super();
    }

    @SuppressWarnings("unchecked")
    @Override
    public SetAttribute<? super Z, E> getModel() {
        return (SetAttribute<? super Z, E>) super.getModel();
    }

    @Override
    public SetJoin<Z, E> on(Expression<Boolean> restriction) {
        return (SetJoin<Z, E>) super.on(restriction);
    }

    @Override
    public SetJoin<Z, E> on(Predicate... restrictions) {
        return (SetJoin<Z, E>) super.on(restrictions);
    }
}
