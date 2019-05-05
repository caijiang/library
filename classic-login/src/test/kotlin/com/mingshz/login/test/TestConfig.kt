package com.mingshz.login.test

import com.mingshz.login.EnableClassicLogin
import me.jiangcai.common.jpa.EnableJpa
import me.jiangcai.common.jpa.JpaPackageScanner
import org.springframework.context.annotation.AdviceMode
import org.springframework.context.annotation.ComponentScan
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
@ComponentScan("com.mingshz.login.test.beans")
@EnableWebMvc
open class TestConfig : JpaPackageScanner {
//    @Bean
//    open fun loginDelegate(): MyLoginDelegate = TestLoginDelegate()
//
//    @Bean
//    open fun secureController(): SecureControllerImpl = SecureControllerImpl(loginDelegate())

    override fun addJpaPackage(prefix: String, set: MutableSet<String>) {
        set.add("com.mingshz.login.test.entity")
    }

//    @Configuration
//    @Order(90)
//    open class Se: WebSecurityConfigurerAdapter(false){
//        override fun configure(http: HttpSecurity) {
//            http.authorizeRequests()
//                .anyRequest().authenticated()
//        }
//    }
}