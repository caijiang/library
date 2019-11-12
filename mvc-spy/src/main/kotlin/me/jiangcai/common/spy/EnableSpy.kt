package me.jiangcai.common.spy

import org.springframework.context.annotation.Import
import java.lang.annotation.Inherited

/**
 * * EnableSpy
 * * apply spyFilter via:
 * ```java
 *  DelegatingFilterProxy filterProxy = new DelegatingFilterProxy();
 *  filterProxy.setTargetBeanName("spyFilter");
 * ```
 * * in Spring Security, it's only allow by role: ROLE_ROOT or ROLE_URI_SPY
 * * view the manager .
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