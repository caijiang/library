package com.mingshz.login.password

import com.mingshz.login.AuthenticationType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.stereotype.Component
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import javax.servlet.Filter

/**
 * @author CJ
 */
@Component
class UsernamePasswordAuthenticationType(
    @Autowired
    private val passwordFilter: PasswordFilter,
    @Autowired
    private val passwordProvider: PasswordProvider
) : AuthenticationType, WebMvcConfigurerAdapter() {

//    private lateinit var filter: PasswordFilter

    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>?) {
        super.configureMessageConverters(converters)
    }

    override fun extendMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        super.extendMessageConverters(converters)
        passwordFilter.converters = converters
    }

    override fun authenticationProvider(): AuthenticationProvider {
        return passwordProvider
    }

    override fun authenticationFilter(): Filter {
        return passwordFilter
    }
}