package me.jiangcai.common.spy

import me.jiangcai.common.spy.demo.DemoConfig
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * 包含独立的一个应用
 * @author CJ
 */
@Configuration
@EnableSpy("/102030")
@Import(DemoConfig::class)
open class SpyTestConfig