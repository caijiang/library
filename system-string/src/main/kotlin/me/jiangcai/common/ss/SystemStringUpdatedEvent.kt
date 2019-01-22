package me.jiangcai.common.ss

/**
 * 一个系统字符串被删除或者被更新
 * @author CJ
 */
data class SystemStringUpdatedEvent(
    val key: String,
    val delete: Boolean = false,
    val newValue: Any? = null
)