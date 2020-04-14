package me.jiangcai.common.wechat.service

import me.jiangcai.common.wechat.WechatApiService
import me.jiangcai.common.wechat.WechatWebUserDetailsService
import me.jiangcai.common.wechat.auth.mini.WechatMiniAuthentication
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
class WechatMiniAuthenticationProvider(
    @Autowired
    private val wechatApiService: WechatApiService,
    @Autowired
    private val applicationContext: ApplicationContext,
    @Suppress("SpringJavaInjectionPointsAutowiringInspection") @Autowired
    private val wechatWebUserDetailsService: WechatWebUserDetailsService
) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication {
        val token = authentication as WechatMiniAuthentication

        val authorization = applicationContext.requestWechatAccountAuthorization(token.request)

        // 检查 Referer 头

        val wechatUser = wechatApiService.queryUserViaMiniAuthorizationCode(
            authorization,
            token.code
        )

        val details = wechatWebUserDetailsService.findByWechatUser(wechatUser, token.request)

        token.authenticatedBy(details)

        return token
    }

    override fun supports(authentication: Class<*>): Boolean {
        return WechatMiniAuthentication::class.java.isAssignableFrom(authentication)
    }
}