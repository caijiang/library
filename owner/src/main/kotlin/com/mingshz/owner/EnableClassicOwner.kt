package com.mingshz.owner

import com.mingshz.owner.support.ClassicOwnerConfig
import org.springframework.context.annotation.Import
import java.lang.annotation.Inherited

/**
 * 启用经典的多业主
 * @author CJ
 */
@Suppress("unused")
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention
@MustBeDocumented
@Inherited
@Import(ClassicOwnerConfig::class, OwnerConfig::class)
@Repeatable
annotation class EnableClassicOwner(
    /**
     * 业主委托系统
     */
    val delegate: String = ""
)