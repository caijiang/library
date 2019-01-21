package me.jiangcai.common.thymeleaf

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

/**
 * @author CJ
 */
@Configuration
@EnableWebMvc
@Import(ThymeleafViewConfig::class)
open class ThymeleafViewConfigTestConfig : WebMvcConfigurerAdapter() {
    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addViewController("/hi")
            .setViewName("thymeleaf:classpath:hi")

    }
}