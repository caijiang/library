package me.jiangcai.crud.row;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * 生成order
 *
 * @author CJ
 */
@FunctionalInterface
public interface OrderGenerator {
    /**
     * @param query 实际的查询
     * @param cb    cb
     * @param root  root
     * @return 支持的排序，可以为空或者为null
     */
    List<Order> toOrder(CriteriaQuery query, CriteriaBuilder cb, Root root);
}
