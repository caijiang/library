package me.jiangcai.common.ext.thread

import org.springframework.context.annotation.Import
import java.lang.annotation.Inherited


@Suppress("unused")
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention
@MustBeDocumented
@Inherited
@Import(ThreadSafeConfig::class, ConfigReader::class)
@Repeatable
annotation class EnableThreadSafe(
    /**
     * name
     */
    val value: String
)