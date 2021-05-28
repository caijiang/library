@file:Suppress("unused")

package me.jiangcai.common.ext.jpa

import org.springframework.data.domain.Example
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.query.EscapeCharacter

/**
 * 将 样本 数据转换成 规范
 */
fun <T> Example<T>.toSpecification(escapeCharacter: EscapeCharacter = EscapeCharacter.DEFAULT): Specification<T> {
    return Specification { root, _, criteriaBuilder ->
        QueryByExamplePredicateBuilder.getPredicate(root, criteriaBuilder, this, escapeCharacter)
    }
}
