package com.mingshz.login.token

import com.mingshz.login.ClassicLoginService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider

/**
 * @author CJ
 */
class TokenProvider(
    @Autowired
    classicLoginService: ClassicLoginService<*>
) : PreAuthenticatedAuthenticationProvider() {
    init {
        setPreAuthenticatedUserDetailsService(classicLoginService)
    }
}