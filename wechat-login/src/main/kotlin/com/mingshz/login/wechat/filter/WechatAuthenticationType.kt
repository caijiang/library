package com.mingshz.login.wechat.filter

import com.mingshz.login.AuthenticationType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.stereotype.Component
import javax.servlet.Filter

/**
 * @author CJ
 */
@Component
class WechatAuthenticationType(
    @Autowired
    private val filter: WechatAuthFilter,
    @Autowired
    private val provider: WechatProvider
) : AuthenticationType {
    override fun authenticationProvider(): AuthenticationProvider = provider

    override fun authenticationFilter(): Filter = filter
}