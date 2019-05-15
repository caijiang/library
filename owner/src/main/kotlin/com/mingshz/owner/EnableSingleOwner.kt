package com.mingshz.owner

import com.mingshz.owner.entity.OwnerEntity
import com.mingshz.owner.support.SingleOwnerConfig
import org.springframework.context.annotation.Import
import java.lang.annotation.Inherited
import kotlin.reflect.KClass

/**
 * 启用单业主系统
 * 必须得告诉系统这个业主是什么模样的
 * @author CJ
 */
@Suppress("unused")
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention
@MustBeDocumented
@Inherited
@Import(SingleOwnerConfig::class, OwnerConfig::class)
@Repeatable
annotation class EnableSingleOwner(
    /**
     * 业主委托系统
     */
    val delegate: String = "",
    /**
     * 一个继承了[OwnerEntity]，并且拥有正确数据的类的全限定名
     */
    val ownerClass: KClass<*>
)