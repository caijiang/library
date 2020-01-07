package me.jiangcai.common.ext

import org.apache.commons.lang3.RandomStringUtils
import java.util.*
import kotlin.random.Random

/**
 * @return 随机URL
 * @author CJ
 */
fun Random.nextHttpURL(): String {
    val stringBuilder = StringBuilder("http://")
    stringBuilder.append(RandomStringUtils.randomAlphabetic(2 + nextInt(4)))
    stringBuilder.append(".")
    stringBuilder.append(RandomStringUtils.randomAlphabetic(2 + nextInt(4)))
    if (nextBoolean()) {
        stringBuilder.append(".")
        stringBuilder.append(RandomStringUtils.randomAlphabetic(2 + nextInt(4)))
    }
    return stringBuilder.toString()
}

/**
 * @return 随机一个域名
 */
fun Random.nextDomain(): String {
    return RandomStringUtils.randomAlphabetic(nextInt(5) + 3) + "." + RandomStringUtils.randomAlphabetic(nextInt(5) + 3)
}

/**
 * @return 随机email 地址
 * @author CJ
 */
fun Random.nextEmailAddress(): String {
    return (RandomStringUtils.randomAlphabetic(nextInt(5) + 3)
            + "@"
            + RandomStringUtils.randomAlphabetic(nextInt(5) + 3)
            + "."
            + RandomStringUtils.randomAlphabetic(nextInt(2) + 2))
}

/**
 * @return 中国随机手机号码
 */
fun Random.nextMobileOfChina(): String {
    val p2 = arrayOf("3", "4", "5", "7", "8")
    return "1" + p2[nextInt(p2.size)] + RandomStringUtils.randomNumeric(9)
}


/**
 * 随机抓取一个数组
 *
 * @param origin    原始数组
 * @param minLength 最小宽度
 * @param <T>       该数组的数据类型
 * @return 随机数组
 */
fun <T> Random.nextArray(origin: Array<T>, minLength: Int): Array<T> {
    //先生成结果数据索引表
    val length = nextInt(origin.size - minLength) + minLength
    val newArray = Arrays.copyOf(origin, length)
    // 抓去唯一随机的结果
    var wheel = -1
    //        System.out.println("do random array");
    for (i in newArray.indices) {
        //最少要留下 length-i-1 个结果
        val seed = nextInt(origin.size - wheel - (newArray.size - i) - 1)
        wheel += seed + 1
        newArray[i] = origin[wheel]
    }
    return newArray
}

/**
 * @return 随机枚举
 */
inline fun <reified T : Enum<T>> Random.nextEnum(): T {
    val vs = T::class.java.enumConstants
    return vs[nextInt(vs.size)]
}

/**
 * @param accept 是否接受这个枚举
 * @return 随机枚举
 */
inline fun <reified T : Enum<T>> Random.nextEnum(accept: ((T) -> Boolean)): T {
    while (true) {
        val v = nextEnum<T>()
        if (accept(v))
            return v
    }
}
