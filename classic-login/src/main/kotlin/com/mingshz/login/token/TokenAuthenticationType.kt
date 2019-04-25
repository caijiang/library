package com.mingshz.login.token

import com.mingshz.login.AuthenticationType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.stereotype.Component
import javax.servlet.Filter

/**
 * @author CJ
 */
@Component
class TokenAuthenticationType(
    @Autowired
    private val filter: TokenFilter,
    @Autowired
    private val provider: TokenProvider
) : AuthenticationType {
    override fun authenticationProvider(): AuthenticationProvider = provider

    override fun authenticationFilter(): Filter = filter
}