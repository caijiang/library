package com.mingshz.login.test

import com.mingshz.login.EnableClassicLogin
import com.mingshz.login.LoginDelegate
import com.mingshz.login.test.bean.TestLoginDelegate
import me.jiangcai.common.jpa.EnableJpa
import me.jiangcai.common.jpa.JpaPackageScanner
import org.springframework.context.annotation.AdviceMode
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.web.servlet.config.annotation.EnableWebMvc

/**
 * @author CJ
 */
@Configuration
@EnableTransactionManagement(mode = AdviceMode.PROXY)
@EnableAspectJAutoProxy
@EnableClassicLogin(
    loginClassName = "com.mingshz.login.test.entity.User"
)
@EnableJpa(
    useH2TempDataSource = true
)
@EnableWebMvc
open class TestConfig : JpaPackageScanner {
    @Bean
    open fun loginDelegate(): LoginDelegate = TestLoginDelegate()

    override fun addJpaPackage(prefix: String, set: MutableSet<String>) {
        set.add("com.mingshz.login.test.entity")
    }
}