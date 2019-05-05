package com.mingshz.login.bean

import com.mingshz.login.ClassicLoginService
import com.mingshz.login.entity.Login
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * 提供修改自己密码的功能。
 *
 * @author CJ
 */
@Controller
class ClassicLoginController(
    @Autowired
    private val classicLoginService: ClassicLoginService<*>
) {

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun changePassword(@AuthenticationPrincipal login: Login, @RequestBody body: Map<String, String>) {
        val originPassword = body["originPassword"] ?: throw IllegalArgumentException("原密码必须输入。")
        val newPassword = body["newPassword"] ?: throw IllegalArgumentException("新密码必须输入。")

        classicLoginService.changePassword(login.id!!, originPassword, newPassword)
    }

}