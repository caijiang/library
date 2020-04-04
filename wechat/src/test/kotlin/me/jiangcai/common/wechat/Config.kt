package me.jiangcai.common.wechat

import me.jiangcai.common.jpa.EnableJpa
import me.jiangcai.common.jpa.JpaPackageScanner
import me.jiangcai.common.wechat.auth.mini.WechatMiniAuthenticationFilter
import me.jiangcai.common.wechat.entity.WechatUser
import me.jiangcai.common.wechat.service.WechatMiniAuthenticationProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.PropertySource
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter
import org.springframework.web.servlet.config.annotation.EnableWebMvc

/**
 * @author CJ
 */
@Configuration
@EnableJpa(
    useH2TempDataSource = true
//    useMysqlDatabase = "library"
)
@EnableWebMvc
@PropertySource("classpath:/test.properties")
@Import(WechatSpringConfig::class, SConfig::class)
class Config : JpaPackageScanner {
    @Bean
    fun wechatWebUserDetailsService(): WechatWebUserDetailsService {
        return object : WechatWebUserDetailsService {
            override fun findByWechatUser(user: WechatUser): UserDetails {
                return object : UserDetails {
                    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
                        return emptyList<GrantedAuthority>().toMutableList()
                    }

                    override fun isEnabled(): Boolean = true

                    override fun getUsername(): String {
                        return user.openId + "|" + user.miniSessionKey
                    }

                    override fun isCredentialsNonExpired(): Boolean = true

                    override fun getPassword(): String {
                        TODO("Not yet implemented")
                    }

                    override fun isAccountNonExpired(): Boolean = true

                    override fun isAccountNonLocked(): Boolean = true

                }
            }

        }
    }

    override fun addJpaPackage(prefix: String, set: MutableSet<String>) {
        set.add("me.jiangcai.common.wechat.entity")
    }
}

@EnableWebSecurity
class SConfig(
    @Autowired
    private val wechatMiniAuthenticationProvider: WechatMiniAuthenticationProvider
) : WebSecurityConfigurerAdapter() {
    @Override
    @Bean
    override fun authenticationManager(): AuthenticationManager {
        return ProviderManager(
            listOf(
                wechatMiniAuthenticationProvider
            )
        )
    }

    @Bean
    fun wechatMiniAuthenticationFilter(): WechatMiniAuthenticationFilter {
        val filter = WechatMiniAuthenticationFilter()
        filter.setAuthenticationManager(authenticationManager())
        filter.setAuthenticationSuccessHandler { _, response, authentication ->
            val details = authentication.details as UserDetails
            response.outputStream.writer().use {
                it.write(details.username)
                it.flush()
            }
        }
        return filter
    }

    override fun configure(http: HttpSecurity) {
        http
            .authorizeRequests()
            .antMatchers("/**").permitAll()
            .and()
            .anonymous()
            .and()
            .addFilterAfter(wechatMiniAuthenticationFilter(), AnonymousAuthenticationFilter::class.java)
            .formLogin().disable()
            .csrf().disable()
    }
}