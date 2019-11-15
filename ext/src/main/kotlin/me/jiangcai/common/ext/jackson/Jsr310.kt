package me.jiangcai.common.ext.jackson

import com.fasterxml.jackson.core.util.VersionUtil
import com.fasterxml.jackson.databind.module.SimpleModule
import java.time.LocalDateTime

/**
 * @author CJ
 */
@Suppress("unused")
class Jsr310 : SimpleModule(
    VersionUtil.parseVersion(
        "1.0.0", "me.jiangcai.common.ext", "fix"
    )
) {
    init {
        addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer())
    }
}