package me.jiangcai.common.wechat.service

import me.jiangcai.common.wechat.WechatApiService
import me.jiangcai.common.wechat.WechatWebUserDetailsService
import me.jiangcai.common.wechat.auth.web.WechatWebAuthentication
import me.jiangcai.common.wechat.requestWechatAccountAuthorization
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

/**
 * @author CJ
 */
@Component
class WechatWebAuthenticationProvider(
    @Autowired
    private val wechatApiService: WechatApiService,
    @Autowired
    private val applicationContext: ApplicationContext,
    @Suppress("SpringJavaInjectionPointsAutowiringInspection") @Autowired
    private val wechatWebUserDetailsService: WechatWebUserDetailsService
) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication {
        val token = authentication as WechatWebAuthentication

        val wechatUser = wechatApiService.queryUserViaAuthorizationCode(
            applicationContext.requestWechatAccountAuthorization(token.request),
            token.code
        )

        val details = wechatWebUserDetailsService.findByWechatUser(wechatUser, token.request)

        token.authenticatedBy(details)

        return token
    }

    override fun supports(authentication: Class<*>): Boolean {
        return WechatWebAuthentication::class.java.isAssignableFrom(authentication)
    }
}