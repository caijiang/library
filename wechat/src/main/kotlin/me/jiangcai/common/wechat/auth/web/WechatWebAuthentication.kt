package me.jiangcai.common.wechat.auth.web

import me.jiangcai.common.ext.open.UrlAware
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import javax.servlet.http.HttpServletRequest

/**
 * 微信网页授权凭据，包括登录前和登录后
 * @author CJ
 */
class WechatWebAuthentication(
    /**
     * 当时的请求
     */
    val request: HttpServletRequest,
    val code: String,
    @Suppress("unused") private val status: String? = null,
    override val url: String? = null,
    private var _name: String? = null,
    private var _authenticated: Boolean = false,
    private var _authorities: Collection<GrantedAuthority> = emptyList(),
    private var userDetails: UserDetails? = null,
    private var _principal: Any? = null
) : Authentication, UrlAware {

    fun authenticatedBy(details: UserDetails) {
        _authorities = details.authorities
        _authenticated = true
        _name = details.username
        userDetails = details
        _principal = details
    }


    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return _authorities.toMutableList()
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {
        _authenticated = isAuthenticated
    }

    override fun getName(): String? {
        return _name
    }

    override fun getCredentials(): Any {
        return code
    }

    override fun getPrincipal(): Any? {
        return _principal
    }

    override fun isAuthenticated(): Boolean {
        return _authenticated
    }

    override fun getDetails(): Any? {
        return userDetails
    }

}