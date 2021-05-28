@file:Suppress("unused")

package me.jiangcai.common.ext

import java.lang.reflect.UndeclaredThrowableException
import java.net.URI

/**
 * @param path 如果是 / 开头则替换掉所有path
 * @return 下一层 path
 */
fun URI.sub(path: String): URI {
    if (path.startsWith("/"))
        return resolve(path)
    val source = toURL().toString()
    if (source.endsWith("/"))
        return URI(source + path)
    return URI("$source/$path")
}


/**
 * @return 最早[delimiter]之前的内容；如果没有整个返回
 */
fun String.onlyPrefixBefore(delimiter: String): String {
    val i = indexOf(delimiter)
    if (i == -1)
        return this
    return substring(0, i)
}

/**
 * @return 最后[delimiter] 之后的内容; 如果没有整个返回
 */
fun String.onlySuffixAfter(delimiter: String): String {
    val i = lastIndexOf(delimiter)
    if (i == -1)
        return this
    return substring(i + 1)
}

/**
 * 尝试运行业务代码，并且捕捉 [UndeclaredThrowableException] 确保最终被抛出的是 [UndeclaredThrowableException.undeclaredThrowable]
 */
fun <R> tryThrowUndeclaredCause(block: () -> R): R {
    try {
        return block()
    } catch (e: UndeclaredThrowableException) {
        throw e.undeclaredThrowable
    }
}
