package me.jiangcai.crud.row;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.util.List;

/**
 * 它定义了可以以列(row,或者叫做记录)方式呈现数据的一种方式
 * 其中包括<ul>
 * <li>数据规格，即哪些实体会被排除</li>
 * <li>列字段，实体中何种数据(并不特指某字段)会被关注</li>
 * </ul>
 *
 * @param <T> 数据来自的JPA Entity范型
 * @author CJ
 */
public interface RowDefinition<T> {

    /**
     * @return 这个定义的名称，支持中文
     */
    default String getName() {
        return null;
    }

    /**
     * @return 最初查询的实体也就是使用哪个 {@link Root}
     */
    Class<T> entityClass();

    /**
     * 该方法是除了{@link #entityClass()}外最早运行的方法
     *
     * @return 所有字段定义
     */
    List<FieldDefinition<T>> fields();

    /**
     * @return 数据规格;可以为null
     */
    Specification<T> specification();

    /**
     * @return 以何表达式作为count参数
     */
    default Expression<?> count(CriteriaQuery<Long> countQuery, CriteriaBuilder criteriaBuilder, Root<T> root) {
        return root;
    }

    /**
     * @param criteriaBuilder cb
     * @param root            root
     * @return 默认排序，如果请求没有明示排序需求；null表示无默认排序
     */
    default List<Order> defaultOrder(CriteriaBuilder criteriaBuilder, Root<T> root) {
        return null;
    }

    /**
     * @param cb    cb
     * @param query query
     * @param root  root
     * @return 数据查询时的分组
     */
    default CriteriaQuery<T> dataGroup(CriteriaBuilder cb, CriteriaQuery<T> query, Root<T> root) {
        return query;
    }

    /**
     * @param cb    cb
     * @param query query
     * @param root  root
     * @return 统计查询时的分组
     */
    default CriteriaQuery<Long> countQuery(CriteriaBuilder cb, CriteriaQuery<Long> query, Root<T> root) {
        return query;
    }

    /**
     * 聚合查询, 每一个结果都必须具备 alias
     *
     * @param cb    cb
     * @param query query
     * @param root  root
     * @return 样本合并查询的结果; null 表示不支持
     */
    default CriteriaQuery<Tuple> sampleQuery(CriteriaBuilder cb, CriteriaQuery<Tuple> query, Root<T> root) {
        return null;
    }
}
