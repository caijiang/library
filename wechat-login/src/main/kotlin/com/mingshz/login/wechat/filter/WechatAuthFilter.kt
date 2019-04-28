package com.mingshz.login.wechat.filter

import com.mingshz.login.wechat.controller.WechatController
import me.jiangcai.wx.standard.entity.support.AppIdOpenID
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest

/**
 * 授权是指，它所获得的微信信息。
 *
 * 授权成功则进行url跳转，因为必然存在一个url参数
 *
 * 更换为 预授权过滤器，一个核心改动是伪造一个AM
 * @author CJ
 */
@Component
class WechatAuthFilter(
    @Autowired
    private val wechatProvider: WechatProvider
//    @Autowired
//    private val manager: AuthenticationManager
) : AbstractPreAuthenticatedProcessingFilter() {

    init {
        setCheckForPrincipalChanges(false)
        setAuthenticationManager {
            wechatProvider.authenticate(WechatAuthenticationToken(it.credentials as AppIdOpenID))
        }
    }

    override fun getPreAuthenticatedCredentials(request: HttpServletRequest): Any? {
        return getPreAuthenticatedPrincipal(request)
    }

    override fun getPreAuthenticatedPrincipal(request: HttpServletRequest): Any? {
        if (request.requestURI != "/wechat/authLogin")
            return null
        return request.session.getAttribute(WechatController.SessionKeyForAppIdOpenID) ?: return null
    }


//    @Autowired
//    private val manager: AuthenticationManager
//) : AbstractAuthenticationProcessingFilter(AntPathRequestMatcher("/wechat/authLogin")) {

//    init {
//        setContinueChainBeforeSuccessfulAuthentication(true)
//        authenticationManager = manager
//
//        setAuthenticationSuccessHandler { request, response, _ ->
////            sendRedirect(request, response)
//        }
//        setAuthenticationFailureHandler { request, response, _ ->
//            sendRedirect(request, response)
//        }
////        setSessionAuthenticationStrategy(
////            SessionFixationProtectionStrategy()
////        )
//    }
//
//    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication? {
//        val id = request.session.getAttribute(WechatController.SessionKeyForAppIdOpenID)
//        // 如果是空的 也应该回去。
//        if (id == null) {
//            sendRedirect(request, response)
//            return null
//        }
//
//        return authenticationManager.authenticate(WechatAuthenticationToken(id as AppIdOpenID))
//    }
//
//    private fun sendRedirect(request: HttpServletRequest, response: HttpServletResponse) {
//        response.sendRedirect(request.getParameter("url"))
//    }
}