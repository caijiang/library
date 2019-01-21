package me.jiangcai.common.ext

/**
 * @author CJ
 */

/**
 * 随机排序
 */
@Suppress("RedundantVisibilityModifier", "unused")
public fun <T> Iterable<T>.randomSort(): List<T> {
    return sortedWith(RandomComparator())
}

