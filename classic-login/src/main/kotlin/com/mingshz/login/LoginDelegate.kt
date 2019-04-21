package com.mingshz.login

import com.mingshz.login.entity.Login
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 由客户端项目提供的登录委托
 * @author CJ
 */
interface LoginDelegate {
    /**
     * 登录成功后
     */
    fun authenticationSuccess(
        request: HttpServletRequest, response: HttpServletResponse
        , authentication: Authentication, login: Login
    )

    /**
     * 登录失败后
     */
    fun authenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    )

}