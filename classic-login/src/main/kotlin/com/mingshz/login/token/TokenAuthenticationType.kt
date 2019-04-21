package com.mingshz.login.token

import com.mingshz.login.AuthenticationType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationProvider
import javax.servlet.Filter

/**
 * @author CJ
 */
class TokenAuthenticationType(
    @Autowired
    private val filter: TokenFilter,
    @Autowired
    private val provider: TokenProvider
) : AuthenticationType {
    override fun authenticationProvider(): AuthenticationProvider = provider

    override fun authenticationFilter(): Filter = filter
}