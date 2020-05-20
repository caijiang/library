package me.jiangcai.common.ext

import java.math.BigDecimal


/**
 * Returns the sum of all values produced by [selector] function applied to each element in
 * the collection.
 */
inline fun <T> Iterable<T>.sumByBigDecimal(selector: (T) -> BigDecimal): BigDecimal {
    var sum: BigDecimal = BigDecimal.ZERO
    for (element in this) {
        sum += selector(element)
    }
    return sum
}