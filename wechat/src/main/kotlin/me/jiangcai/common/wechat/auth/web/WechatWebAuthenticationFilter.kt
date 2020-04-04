package me.jiangcai.common.wechat.auth.web

import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 非常简单暴力，只要有 code state 都可以
 * @param domainPrefix 支持的域名前置, 空的话就不限制域名
 * @param defaultFilterProcessesUrl 处理地址 默认 /wechatAuth
 * @author CJ
 */
class WechatWebAuthenticationFilter(
    private val domainPrefix: String? = null,
    defaultFilterProcessesUrl: String = "/wechatAuth"
) :
    AbstractAuthenticationProcessingFilter(defaultFilterProcessesUrl) {

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse?): Authentication? {
        val code = request.getParameter("code") ?: return null
        val token = WechatWebAuthentication(
            request = request,
            code = code,
            status = request.getParameter("status"),
            url = request.getParameter("url")
        )

        return authenticationManager.authenticate(token)
    }

    override fun doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) {
        if (domainPrefix == null) {
            super.doFilter(req, res, chain)
        } else {
            if (req.serverName.startsWith(domainPrefix))
                super.doFilter(req, res, chain)
            else
                chain.doFilter(req, res)
        }

    }


}