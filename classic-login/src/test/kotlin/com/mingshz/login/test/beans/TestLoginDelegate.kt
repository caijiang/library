package com.mingshz.login.test.beans

import com.mingshz.login.entity.Login
import com.mingshz.login.test.MyLoginDelegate
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author CJ
 */
@Service
open class TestLoginDelegate : MyLoginDelegate {

    //    @PreAuthorize("hasAnyRole('ROOT')")
    override fun wellDone() {
    }

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