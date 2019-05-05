package com.mingshz.login

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.csrf.CsrfFilter
import org.springframework.web.filter.CharacterEncodingFilter

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true, securedEnabled = true)
@Order(Ordered.HIGHEST_PRECEDENCE)
internal open class ClassicLoginSecurityConfig(
    @Suppress("SpringJavaInjectionPointsAutowiringInspection") @Autowired
    private val authenticationManager: AuthenticationManager,
    @Autowired
    private val applicationContext: ApplicationContext
) : WebSecurityConfigurerAdapter() {

    override fun authenticationManager(): AuthenticationManager {
        return authenticationManager
    }

    override fun configure(builder: WebSecurity) {
        applicationContext.getBeansOfType(CustomSecurity::class.java)
            .values
            .forEach {
                it.configure(builder)
            }
    }

//    override fun configure(auth: AuthenticationManagerBuilder?) {
//        super.configure(auth)
//    }
//
//    override fun init(web: WebSecurity?) {
//        super.init(web)
//    }

    override fun configure(http: HttpSecurity) {
//        super.configure(http)
        val filter = CharacterEncodingFilter()
        filter.encoding = "UTF-8"
        filter.setForceEncoding(true)
        http.addFilterBefore(filter, CsrfFilter::class.java)

        // 整出我们的filter
//        http.

        var registry = http
            .authorizeRequests()
            .antMatchers("/test", ClassicLoginConfig.loginUri).permitAll()

        applicationContext.getBeansOfType(CustomSecurity::class.java)
            .values
            .forEach {
                registry = it.configure(registry)
            }


        var x = registry
            .anyRequest()
            .authenticated()
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            .and()
//            .formLogin().disable()
            .csrf().disable()
//            .antMatcher("/**")
//            .authorizeRequests()

        applicationContext.getBeansOfType(CustomSecurity::class.java)
            .values
            .forEach {
                x = it.configure(x)
            }

        applicationContext.getBeansOfType(AuthenticationType::class.java)
            .values
            .forEach {
                http.addFilterBefore(it.authenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
            }
    }

}
