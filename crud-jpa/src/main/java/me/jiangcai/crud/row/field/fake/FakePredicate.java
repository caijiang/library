package me.jiangcai.crud.row.field.fake;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.util.List;

/**
 * @author CJ
 */
public class FakePredicate extends FakeExpression<Boolean> implements Predicate {

    public FakePredicate() {
    }

    public FakePredicate(AbstractFake fake) {
        super(fake);
    }

    @Override
    public BooleanOperator getOperator() {
        throw new NoSuchMethodError("");
    }

    @Override
    public boolean isNegated() {
        throw new NoSuchMethodError("");
    }

    @Override
    public List<Expression<Boolean>> getExpressions() {
        throw new NoSuchMethodError("");
    }

    @Override
    public Predicate not() {
        andThen(obj -> {
            Boolean v = (Boolean) obj;
            return !v;
        });
        return this;
    }
}
