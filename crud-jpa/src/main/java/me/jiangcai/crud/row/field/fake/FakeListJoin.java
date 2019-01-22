package me.jiangcai.crud.row.field.fake;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.ListAttribute;
import java.util.List;

/**
 * @author CJ
 */
public class FakeListJoin<Z, E>
        extends FakePluralJoin<Z, List<E>, E> implements ListJoin<Z, E> {
    public FakeListJoin(AbstractFake fake) {
        super(fake);
    }

    public FakeListJoin() {
        super();
    }

    @SuppressWarnings("unchecked")
    @Override
    public ListAttribute<? super Z, E> getModel() {
        return (ListAttribute<? super Z, E>) super.getModel();
    }

    @Override
    public ListJoin<Z, E> on(Expression<Boolean> restriction) {
        return (ListJoin<Z, E>) super.on(restriction);
    }

    @Override
    public ListJoin<Z, E> on(Predicate... restrictions) {
        return (ListJoin<Z, E>) super.on(restrictions);
    }

    @Override
    public Expression<Integer> index() {
        throw new NoSuchMethodError("index 到底是什么意思？");
    }
}
