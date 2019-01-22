package me.jiangcai.crud.row;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

/**
 * 字段定义
 *
 * @param <T> 它可以处理的root的类型
 * @author CJ
 */
public interface FieldDefinition<T> extends IndefiniteFieldDefinition<T> {
    /**
     * @param criteriaBuilder cb
     * @param query           查询
     * @param root            from
     * @return 要作为结果集的目标;null 表示本字段依赖其他字段的查询结果，通常这个组合里只有一个是非null的
     */
    javax.persistence.criteria.Selection<?> select(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query, Root<T> root);


    /**
     * @param query           实际的查询
     * @param criteriaBuilder cb
     * @param root            root
     * @return 排序表达式; null 表示该字段并不支持排序
     */
    Expression<?> order(CriteriaQuery query, CriteriaBuilder criteriaBuilder, Root<T> root);
}
