@file:Suppress("unused")

package me.jiangcai.common.ext.json

import com.jayway.jsonpath.Predicate
import java.io.File
import java.io.InputStream

/**
 * @see com.jayway.jsonpath.JsonPath.read
 */
fun <X> String?.readJsonPath(path: String, vararg filters: Predicate): X? {
    return com.jayway.jsonpath.JsonPath.read<X>(this, path, *filters)
}

/**
 * @see com.jayway.jsonpath.JsonPath.read
 */
fun <X> InputStream.readJsonPath(path: String, vararg filters: Predicate): X? {
    return com.jayway.jsonpath.JsonPath.read<X>(this, path, *filters)
}

/**
 * @see com.jayway.jsonpath.JsonPath.read
 */
fun <X> File.readJsonPath(path: String, vararg filters: Predicate): X? {
    return com.jayway.jsonpath.JsonPath.read<X>(this, path, *filters)
}
