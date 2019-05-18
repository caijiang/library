package me.jiangcai.crud.env.controller

import com.mingshz.login.LoginDelegate
import com.mingshz.login.entity.Login
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author CJ
 */
@Service
class LoginDelegateImpl : LoginDelegate {
    override fun authenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
        login: Login
    ) {
    }

    override fun authenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
    }
}