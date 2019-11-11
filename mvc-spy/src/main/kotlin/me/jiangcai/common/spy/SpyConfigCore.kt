package me.jiangcai.common.spy

import me.jiangcai.common.thymeleaf.ThymeleafViewConfig
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * @author CJ
 */
@Configuration
@ComponentScan("me.jiangcai.common.spy.bean")
@Import(ThymeleafViewConfig::class, SpyFilter::class)
open class SpyConfigCore(
    @Suppress("SpringJavaInjectionPointsAutowiringInspection") val uri: String
)