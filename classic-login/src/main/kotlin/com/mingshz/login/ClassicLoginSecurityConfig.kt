package com.mingshz.login

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.csrf.CsrfFilter
import org.springframework.web.filter.CharacterEncodingFilter

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
internal open class ClassicLoginSecurityConfig(
    @Autowired
    private val applicationContext: ApplicationContext
) : WebSecurityConfigurerAdapter() {

    override fun configure(builder: WebSecurity) {

    }

    override fun configure(http: HttpSecurity) {
//        super.configure(http)

        val filter = CharacterEncodingFilter()
        filter.encoding = "UTF-8"
        filter.setForceEncoding(true)
        http.addFilterBefore(filter, CsrfFilter::class.java)

        // 整出我们的filter
//        http.

        http
            .authorizeRequests()
            .anyRequest().authenticated()
            .antMatchers(ClassicLoginConfig.loginUri).permitAll()
            .and()
//            .formLogin().disable()
            .csrf().disable()
//            .antMatcher("/**")
//            .authorizeRequests()


        applicationContext.getBeansOfType(AuthenticationType::class.java)
            .values
            .forEach {
                http.addFilterBefore(it.authenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
            }
    }

}
