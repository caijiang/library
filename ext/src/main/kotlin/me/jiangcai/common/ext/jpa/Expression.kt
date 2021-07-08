@file:Suppress("unused")

package me.jiangcai.common.ext.jpa

import javax.persistence.criteria.From
import javax.persistence.criteria.Join
import javax.persistence.criteria.JoinType
import javax.persistence.criteria.SetJoin
import javax.persistence.metamodel.SetAttribute
import javax.persistence.metamodel.SingularAttribute

/**
 * @return 查看现在是否已经连接，没有的化 则创建连接
 */
fun <Z, X, T> From<Z, X>.joinAbsent(attribute: SingularAttribute<X, T>, type: JoinType? = null): Join<X, T> {
    @Suppress("UNCHECKED_CAST")
    val joined = joins.find {
        it.attribute === attribute
    } as Join<X, T>?
    return joined ?: if (type != null) join(attribute, type) else join(attribute)
}

/**
 * @return 查看现在是否已经连接，没有的化 则创建连接
 */
fun <Z, X, T> From<Z, X>.joinAbsent(attribute: SetAttribute<X, T>, type: JoinType? = null): SetJoin<X, T> {
    @Suppress("UNCHECKED_CAST")
    val joined = joins.find {
        it.attribute === attribute
    } as SetJoin<X, T>?
    return joined ?: if (type != null) join(attribute, type) else join(attribute)
}
