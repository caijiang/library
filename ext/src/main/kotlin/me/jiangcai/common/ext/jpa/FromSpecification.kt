@file:Suppress("unused")

package me.jiangcai.common.ext.jpa

import org.springframework.data.jpa.domain.Specification
import org.springframework.lang.Nullable
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.From
import javax.persistence.criteria.Predicate

/**
 * 形同 [Specification] 区别在于跟[javax.persistence.criteria.Root] 解耦
 */
typealias FromSpecification<T> = (from: From<*, T>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder) -> Predicate?

fun <T> FromSpecification<T>.toSpecification(): Specification<T> {
    return Specification<T> { root, query, criteriaBuilder ->
        invoke(root, query, criteriaBuilder)
    }
}

fun <Z, T> FromSpecification<T>.withIn(converter: (from: From<*, Z>, criteriaBuilder: CriteriaBuilder) -> From<*, T>): FromSpecification<Z> {
    return { from, query, criteriaBuilder ->
        invoke(converter(from, criteriaBuilder), query, criteriaBuilder)
    }
}


fun <T> FromSpecification<T>.and(@Nullable other: FromSpecification<T>?): FromSpecification<T>? {
    return composed(
        this, other
    ) { builder, left, rhs ->
        builder.and(
            left,
            rhs
        )
    }
}

fun <T> FromSpecification<T>.or(@Nullable other: FromSpecification<T>?): FromSpecification<T>? {
    return composed(
        this, other
    ) { builder, left, rhs ->
        builder.or(
            left,
            rhs
        )
    }
}

private typealias Combiner = (builder: CriteriaBuilder, lhs: Predicate?, rhs: Predicate?) -> Predicate

private fun <T> composed(
    @Nullable lhs: FromSpecification<T>?, @Nullable rhs: FromSpecification<T>?,
    combiner: Combiner
): FromSpecification<T>? {
    if (lhs == null && rhs == null)
        return null
    return { from, query, builder ->
        val otherPredicate =
            toPredicate(lhs, from, query, builder)
        val thisPredicate =
            toPredicate(rhs, from, query, builder)
        when {
            thisPredicate == null -> otherPredicate
            otherPredicate == null -> thisPredicate
            else -> combiner(builder, thisPredicate, otherPredicate)
        }
    }
}

private fun <T> toPredicate(
    specification: FromSpecification<T>?, from: From<*, T>, query: CriteriaQuery<*>,
    builder: CriteriaBuilder
): Predicate? {
    return specification?.invoke(from, query, builder)
}