package me.jiangcai.crud.row.field.fake;

import javax.persistence.criteria.*;
import javax.persistence.metamodel.EntityType;
import java.util.List;
import java.util.Set;

/**
 * @author CJ
 */
public class FakeCriteriaQuery implements CriteriaQuery {
    @Override
    public CriteriaQuery select(Selection selection) {
        return null;
    }

    @Override
    public Root from(Class entityClass) {
        return null;
    }

    @Override
    public Root from(EntityType entity) {
        return null;
    }

    @Override
    public CriteriaQuery where(Predicate... restrictions) {
        return null;
    }

    @Override
    public CriteriaQuery having(Predicate... restrictions) {
        return null;
    }

    @Override
    public CriteriaQuery orderBy(Order... o) {
        return null;
    }

    @Override
    public CriteriaQuery distinct(boolean distinct) {
        return null;
    }

    @Override
    public Set<Root<?>> getRoots() {
        return null;
    }

    @Override
    public Selection getSelection() {
        return null;
    }

    @Override
    public List<Expression<?>> getGroupList() {
        return null;
    }

    @Override
    public Predicate getGroupRestriction() {
        return null;
    }

    @Override
    public boolean isDistinct() {
        return false;
    }

    @Override
    public Class getResultType() {
        return null;
    }

    @Override
    public List<Order> getOrderList() {
        return null;
    }

    @Override
    public Set<ParameterExpression<?>> getParameters() {
        return null;
    }

    @Override
    public CriteriaQuery orderBy(List o) {
        return null;
    }

    @Override
    public CriteriaQuery having(Expression restriction) {
        return null;
    }

    @Override
    public CriteriaQuery groupBy(List grouping) {
        return null;
    }

    @Override
    public CriteriaQuery groupBy(Expression[] grouping) {
        return null;
    }

    @Override
    public CriteriaQuery where(Expression restriction) {
        return null;
    }

    @Override
    public CriteriaQuery multiselect(List list) {
        return null;
    }

    @Override
    public CriteriaQuery multiselect(Selection[] selections) {
        return null;
    }

    @Override
    public <U> Subquery<U> subquery(Class<U> type) {
        return null;
    }

    @Override
    public Predicate getRestriction() {
        return null;
    }
}
