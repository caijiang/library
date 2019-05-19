package me.jiangcai.crud.row

import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Root

/**
 * 特殊的一种[FieldDefinition],它对于自己的最终类型是明确的。
 * @author CJ
 */
interface TypeFieldDefinition<X, T> : FieldDefinition<T> {

    /**
     * 组中数据类型
     */
    fun getResultType(): Class<X>

    override fun select(cb: CriteriaBuilder, query: CriteriaQuery<*>, root: Root<T>): Expression<out X>

}