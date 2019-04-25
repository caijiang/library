package com.mingshz.login.token

import com.mingshz.login.ClassicLoginService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider
import org.springframework.stereotype.Component

/**
 * @author CJ
 */
@Component
class TokenProvider(
    @Autowired
    classicLoginService: ClassicLoginService<*>
) : PreAuthenticatedAuthenticationProvider() {
    init {
        setPreAuthenticatedUserDetailsService(classicLoginService)
    }
}