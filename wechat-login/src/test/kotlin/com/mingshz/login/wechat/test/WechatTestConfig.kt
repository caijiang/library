package com.mingshz.login.wechat.test

import com.mingshz.login.CustomSecurity
import com.mingshz.login.EnableClassicLogin
import com.mingshz.login.LoginDelegate
import com.mingshz.login.test.beans.SecureControllerImpl
import com.mingshz.login.test.beans.TestLoginDelegate
import com.mingshz.login.wechat.WechatLoginConfig
import me.jiangcai.common.jpa.EnableJpa
import me.jiangcai.common.jpa.JpaPackageScanner
import me.jiangcai.wx.test.WeixinTestConfig
import org.springframework.context.annotation.*
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.web.servlet.config.annotation.EnableWebMvc

/**
 * @author CJ
 */
@Configuration
//@PropertySource("classpath:/test_wx.properties")
@EnableClassicLogin(
    loginClassName = "com.mingshz.login.test.entity.User",
    loginExtraConfigClasses = ["com.mingshz.login.wechat.WechatLoginConfigPrefix"]
)
@EnableTransactionManagement(mode = AdviceMode.PROXY)
@EnableAspectJAutoProxy
@EnableJpa(
    useH2TempDataSource = true
)
@EnableWebMvc
@Import(WechatLoginConfig::class, WeixinTestConfig::class)
open class WechatTestConfig : JpaPackageScanner, CustomSecurity {
    override fun configure(httpSecurity: HttpSecurity): HttpSecurity {
        return httpSecurity
    }

    override fun configure(registry: ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry): ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry {
        return registry.antMatchers("/bind/**").permitAll()
    }

    override fun configure(webSecurity: WebSecurity) {
    }

    @Bean
    open fun myWeixinPublicAccount(): MyWeixinPublicAccount = MyWeixinPublicAccount()

    @Bean
    open fun loginDelegate(): LoginDelegate = TestLoginDelegate()

    @Bean
    open fun secureController(): SecureControllerImpl = SecureControllerImpl()

    @Bean
    open fun bindController(): BindController = BindController()

    override fun addJpaPackage(prefix: String, set: MutableSet<String>) {
        set.add("com.mingshz.login.test.entity")
    }
}