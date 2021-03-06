package me.jiangcai.common.ss

import me.jiangcai.common.thymeleaf.ThymeleafViewConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

/**
 * 支持配置:
 * * jiangcai.ss.uri 配置管理uri, 比如 /ss
 *
 * 如果安全机制被加载，那么它将要求[SystemStringConfig.MANAGER_ROLE]权限进行管理
 *
 * @author CJ
 */
@Configuration
@Import(ThymeleafViewConfig::class)
@ComponentScan("me.jiangcai.common.ss.impl")
@EnableJpaRepositories("me.jiangcai.common.ss.repository")
open class SystemStringConfig {
    companion object {
        const val MANAGER_ROLE = "_M_CJ_SYSTEM_STRING"
    }

    @Autowired(required = false)
    private var messageSource: MessageSource? = null

    @Bean
    open fun sysMessageSource(): MessageSource {
        val messageSource = ResourceBundleMessageSource()
        messageSource.parentMessageSource = this.messageSource
        messageSource.setBasename("me/jiangcai/common/sysMessage")
        messageSource.setDefaultEncoding("UTF-8")
        return messageSource
    }

}