package me.jiangcai.common.test.classic

import com.mingshz.login.entity.Login
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

/**
 * @author CJ
 */
class LoginAuthentication(private val login: Login) : Authentication {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = login.authorities

    override fun setAuthenticated(isAuthenticated: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getName(): String? = login.name

    override fun getCredentials(): Any? = login.password

    override fun getPrincipal(): Any = login

    override fun isAuthenticated(): Boolean = true

    override fun getDetails(): Any = login
}