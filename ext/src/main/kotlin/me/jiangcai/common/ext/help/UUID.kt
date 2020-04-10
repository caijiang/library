package me.jiangcai.common.ext.help

import java.util.*

/**
 * @return 没有-的UUID
 */
fun UUID.toSimpleString(): String {
    return toString().replace("-", "")
}