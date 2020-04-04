package me.jiangcai.common.wechat.auth.mini

import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @param domainPrefix 支持的域名前置, 空的话就不限制域名
 * @param defaultFilterProcessesUrl 处理地址 默认 /loginAsWechatApp
 * @author CJ
 */
class WechatMiniAuthenticationFilter(
    private val domainPrefix: String? = null,
    defaultFilterProcessesUrl: String = "/loginAsWechatApp"
) :
    AbstractAuthenticationProcessingFilter(defaultFilterProcessesUrl) {

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse?): Authentication? {
        val code = request.getParameter("code") ?: return null
        val token = WechatMiniAuthentication(
            request = request,
            code = code
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