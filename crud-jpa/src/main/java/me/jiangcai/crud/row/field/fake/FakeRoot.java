package me.jiangcai.crud.row.field.fake;

import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;

/**
 * @author CJ
 */
public class FakeRoot<X> extends FakeFrom<X, X> implements Root<X> {

    public FakeRoot(AbstractFake fake) {
        super(fake);
    }

    public FakeRoot() {
        super();
    }

    @Override
    public EntityType<X> getModel() {
        return (EntityType<X>) super.getModel();
    }
}
