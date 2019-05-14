package me.jiangcai.common.ext.thread.job

import me.jiangcai.common.ext.thread.EnableThreadSafe
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

/**
 * @author CJ
 */
@Configuration
@EnableWebMvc
//@ComponentScan("me.jiangcai.common.ext.thread.job")
@EnableThreadSafe("demo2")
open
class Config : WebMvcConfigurerAdapter() {

    override fun addViewControllers(registry: ViewControllerRegistry?) {
        super.addViewControllers(registry)
        registry?.addStatusController("/hello", HttpStatus.ACCEPTED)
    }
}