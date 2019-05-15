package com.mingshz.owner

import com.mingshz.owner.support.OwnerEntityResolver
import me.jiangcai.common.jpa.JpaPackageScanner
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

/**
 * 需要提供[FindOwnerService]
 * 启动业主系统
 * @author CJ
 */
@Configuration
@EnableWebMvc
open class OwnerConfig : JpaPackageScanner, WebMvcConfigurerAdapter() {
    override fun addJpaPackage(prefix: String, set: MutableSet<String>) {
        set.add("com.mingshz.owner.entity")
    }

    override fun addArgumentResolvers(argumentResolvers: MutableList<HandlerMethodArgumentResolver>?) {
        super.addArgumentResolvers(argumentResolvers)
        argumentResolvers?.add(OwnerEntityResolver())
    }

}