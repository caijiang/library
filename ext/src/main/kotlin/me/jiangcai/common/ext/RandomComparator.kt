package me.jiangcai.common.ext

import kotlin.random.Random


/**
 * @author CJ
 */
class RandomComparator<T> : Comparator<T> {
    override fun compare(o1: T?, o2: T?): Int {
        return random.nextInt()
    }

    private var random = Random(System.currentTimeMillis())

}
