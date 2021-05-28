@file:Suppress("unused")

package me.jiangcai.common.ext.jpa

import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Expression

/**
 * @return 计算给予表达式所有的合计
 */
fun <X : Number> CriteriaBuilder.sumSome(vararg expr: Expression<out X>): Expression<X> {
    if (expr.size <= 1) throw IllegalArgumentException("can not accept SINGLE expr.")
    if (expr.size == 2)
        return sum(expr[0], expr[1])
    val (e1, e2) = expr
    return sumSome(sum(e1, e2), *expr.drop(2).toTypedArray())
}