package me.jiangcai.crud

import org.springframework.context.annotation.Import
import java.lang.annotation.Inherited

/**
 * @author CJ
 */
@Suppress("unused")
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention
@MustBeDocumented
@Inherited
@Import(CrudConfig::class)
@Repeatable
annotation class EnableCrud