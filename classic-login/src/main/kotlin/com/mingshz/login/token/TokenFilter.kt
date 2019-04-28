package com.mingshz.login.token

import com.mingshz.login.ClassicLoginConfig
import com.mingshz.login.entity.LoginToken
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.DependsOn
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest

/**
 * @author CJ
 */
@Component
@DependsOn("authenticationManager")
class TokenFilter : ApplicationContextAware, AbstractPreAuthenticatedProcessingFilter() {

    init {
        setCheckForPrincipalChanges(true)
        setContinueFilterChainOnUnsuccessfulAuthentication(true)
    }

    override fun principalChanged(request: HttpServletRequest, currentAuthentication: Authentication?): Boolean {
        return getPreAuthenticatedCredentials(request) != null
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        setAuthenticationManager(applicationContext.getBean(AuthenticationManager::class.java))
    }

    override fun getPreAuthenticatedCredentials(request: HttpServletRequest): Any? {
        return LoginToken.fromFullToCredentials(request.getParameter(ClassicLoginConfig.forceAuthenticationTokenParameterName))
    }

    override fun getPreAuthenticatedPrincipal(request: HttpServletRequest): Any? {
        return LoginToken.fromFullToPrincipal(request.getParameter(ClassicLoginConfig.forceAuthenticationTokenParameterName))
    }
}