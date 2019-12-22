package me.jiangcai.crud

import com.mingshz.login.EnableClassicLogin
import me.jiangcai.common.jpa.JpaPackageScanner
import me.jiangcai.crud.env.entity2.User
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
@EnableWebMvc
@EnableAspectJAutoProxy
//    @EnableJpaRepositories
@EnableTransactionManagement(mode = AdviceMode.PROXY)
@ComponentScan("me.jiangcai.crud.env.controller")
@EnableClassicLogin(loginClass = User::class)
//@EnableJpa(useH2TempDataSource = true)
@EnableCrud
open class Test2Config : JpaPackageScanner {
    override fun addJpaPackage(prefix: String, set: MutableSet<String>) {
        set.add("me.jiangcai.crud.env.entity2")
        set.add("me.jiangcai.crud.env.entity")
    }
}