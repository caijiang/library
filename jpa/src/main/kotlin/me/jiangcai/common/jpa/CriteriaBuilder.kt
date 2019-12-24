package me.jiangcai.common.jpa

import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Expression


fun CriteriaBuilder.groupConcat(stringExpr: Expression<String>): Expression<out String> {
    return function("group_concat", String::class.java, stringExpr)
}
