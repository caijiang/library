package com.mingshz.login.wechat.filter

import com.mingshz.login.wechat.WechatLoginService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

/**
 * @author CJ
 */
@Component
class WechatProvider(
    @Autowired
    private val wechatLoginService: WechatLoginService
) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication {
        authentication as WechatAuthenticationToken

        val login = wechatLoginService.findLoginById(authentication.id) ?: throw BadCredentialsException(
            "No-Login Found for ${authentication.id}."
        )

        val result = WechatAuthenticationToken(authentication.id, login)
        result.details = login
        result.isAuthenticated = true

        return result
    }

    override fun supports(authentication: Class<*>): Boolean {
        return authentication == WechatAuthenticationToken::class.java
    }
}