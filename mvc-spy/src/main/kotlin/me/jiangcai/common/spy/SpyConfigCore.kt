package me.jiangcai.common.spy

import me.jiangcai.common.thymeleaf.ThymeleafViewConfig
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import java.util.*

/**
 * @author CJ
 */
@Configuration
@ComponentScan("me.jiangcai.common.spy.bean")
@Import(ThymeleafViewConfig::class, SpyFilter::class)
open class SpyConfigCore(
    @Suppress("SpringJavaInjectionPointsAutowiringInspection") val uri: String = "/manage/spy"
) : BeanFactoryPostProcessor {
    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        val ps = beanFactory.getSingleton("systemProperties") as Properties
        ps["me.jiangcai.common.spy.uri"] = uri
    }
}