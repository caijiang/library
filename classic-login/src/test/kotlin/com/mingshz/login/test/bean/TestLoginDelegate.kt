package com.mingshz.login.test.bean

import com.mingshz.login.LoginDelegate
import com.mingshz.login.entity.Login
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author CJ
 */
class TestLoginDelegate : LoginDelegate {
    override fun authenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
        login: Login
    ) {
        response.contentType = "text/plain"
        response.writer.use { writer ->
            writer.write(login.username)
            writer.flush()
        }
//        response.sendError(200)
    }

    override fun authenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        response.sendError(401, exception.localizedMessage)
    }
}