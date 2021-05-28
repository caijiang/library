package me.jiangcai.common.ext.jackson

import com.fasterxml.jackson.core.util.VersionUtil
import com.fasterxml.jackson.databind.module.SimpleModule
import java.time.LocalDateTime

/**
 * 不要再用这个玩意儿了，用[com.fasterxml.jackson.datatype.jsr310.JavaTimeModule]代替
 * @author CJ
 */
@Suppress("unused")
@Deprecated("使用 com.fasterxml.jackson.datatype.jsr310.JavaTimeModule 代替")
class Jsr310 : SimpleModule(
    VersionUtil.parseVersion(
        "1.0.0", "me.jiangcai.common.ext", "fix"
    )
) {
    init {
        addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer())
    }
}