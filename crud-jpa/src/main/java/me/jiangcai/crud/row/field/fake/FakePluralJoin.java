package me.jiangcai.crud.row.field.fake;

import javax.persistence.criteria.PluralJoin;
import javax.persistence.metamodel.PluralAttribute;

/**
 * @author CJ
 */
public class FakePluralJoin<Z, C, E> extends FakeJoin<Z, E> implements PluralJoin<Z, C, E> {
    public FakePluralJoin(AbstractFake fake) {
        super(fake);
    }

    public FakePluralJoin() {
        super();
    }

    @Override
    public PluralAttribute<? super Z, C, E> getModel() {
        return (PluralAttribute<? super Z, C, E>) super.getModel();
    }

}
