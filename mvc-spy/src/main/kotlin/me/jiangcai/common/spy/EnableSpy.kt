package me.jiangcai.common.spy

import org.springframework.context.annotation.Import
import java.lang.annotation.Inherited

/**
 * 1. EnableSpy
 * 2. apply spyFilter via:
 * ```java
 *  DelegatingFilterProxy filterProxy = new DelegatingFilterProxy();
 *  filterProxy.setTargetBeanName("spyFilter");
 *
 * ```
 * @author CJ
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention
@MustBeDocumented
@Inherited
@Import(SpyConfig::class)
@Repeatable
annotation class EnableSpy(
    /**
     * the URI used to spy
     */
    val value: String = "/manage/spy"
)