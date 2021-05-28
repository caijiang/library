@file:Suppress("unused")

package me.jiangcai.common.ext.jpa

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.query.QueryUtils
import java.math.BigDecimal
import javax.persistence.EntityManager
import javax.persistence.Tuple
import javax.persistence.criteria.*
import kotlin.reflect.KClass


/**
 * 自定义的低阶 [Page] 查询工具
 * @param entityType 主实体类别
 * @param pageable 分页依据
 * @param dataCreator 构建选择项目和排序
 * @param transform 最终数据构造器
 * @param specification 统一数据规格
 */
fun <T : Any, Y> EntityManager.customPage(
    entityType: KClass<T>,
    pageable: Pageable,
    dataCreator: (Root<T>, CriteriaQuery<Tuple>, CriteriaBuilder) -> Pair<List<Selection<*>>, List<Order>?>,
    transform: (Tuple) -> Y,
    countTarget: (Root<T>, CriteriaQuery<*>, CriteriaBuilder) -> Expression<Long> = { root, _, cb ->
        cb.countDistinct(
            root
        )
    },
    specification: Specification<T>? = null
): Page<Y> {
    val cb = criteriaBuilder
    val cq = cb.createTupleQuery()
    val cqRoot = cq.from(entityType.java)
    val countQ = cb.createQuery(Long::class.java)
    val countRoot = countQ.from(entityType.java)

    // 查询总量
    val total = createQuery(
        countQ.select(countTarget(countRoot, countQ, cb)).apply {
            if (specification != null)
                where(specification.toPredicate(countRoot, countQ, cb))
        }
    ).singleResult ?: 0L

    // 查询数据
    val (selections, orders) = dataCreator(cqRoot, cq, cb)
    val data = createQuery(
        cq.multiselect(selections).apply {
            if (specification != null) where(specification.toPredicate(cqRoot, cq, cb))

            if (orders != null) {
                orderBy(orders)
            } else if (!pageable.sort.isUnsorted) {
                orderBy(QueryUtils.toOrders(pageable.sort, cqRoot, cb))
            }
        }
    )
        .setFirstResult(pageable.offset.toInt())
        .setMaxResults(pageable.pageSize)
        .resultList.map(transform)

    return PageImpl(
        data, pageable, total
    )
}

fun CriteriaBuilder.coalesceSum(path: Expression<Long>, defaultValue: Long = 0L): Expression<Long> {
    return coalesce(sum(path), defaultValue)
}

fun CriteriaBuilder.coalesceSum(
    path: Expression<BigDecimal>,
    defaultValue: BigDecimal = BigDecimal.ZERO
): Expression<BigDecimal> {
    return coalesce(sum(path), defaultValue)
}
