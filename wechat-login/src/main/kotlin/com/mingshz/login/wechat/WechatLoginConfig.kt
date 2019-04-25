package com.mingshz.login.wechat

import com.mingshz.login.CustomSecurity
import me.jiangcai.wx.WeixinSpringConfig
import me.jiangcai.wx.standard.StandardWeixinConfig
import me.jiangcai.wx.web.WeixinWebSpringConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer

/**
 * @author CJ
 */
//@DependsOn("com.mingshz.login.ClassicLoginSecurityConfig")
@Configuration
@ComponentScan("com.mingshz.login.wechat.controller", "com.mingshz.login.wechat.service")
@EnableWebSecurity
@Import(WeixinSpringConfig::class, WeixinWebSpringConfig::class, StandardWeixinConfig::class)
@Order(120)
open class WechatLoginConfig(
    @Suppress("SpringJavaInjectionPointsAutowiringInspection") @Autowired
    private val authenticationManager: AuthenticationManager
) : CustomSecurity {
    override fun configure(httpSecurity: HttpSecurity): HttpSecurity {
        return httpSecurity
    }

    override fun configure(registry: ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry)
            : ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry {
        return registry.antMatchers("/wechat/**").permitAll()
    }

    override fun configure(webSecurity: WebSecurity) {
    }

//
//    init {
//        println(1)
//    }
//
//    override fun authenticationManager(): AuthenticationManager {
//        return authenticationManager
//    }
//
//    override fun init(web: WebSecurity) {
////        super.init(web)
////        web.ignoring()
////            .antMatchers("/wechat/**")
//    }
//
//    override fun configure(http: HttpSecurity) {
////        http
////            .authorizeRequests()
////            .antMatchers("/wechat/**").permitAll()
////            .anyRequest().permitAll()
//    }
}

