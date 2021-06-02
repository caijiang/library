package me.jiangcai.common.ext.misc

import me.jiangcai.common.ext.help.runAsRoot
import org.springframework.http.HttpStatus
import org.springframework.lang.Nullable
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.server.ResponseStatusException

/**
 * 允许当前登录用户修改自己的密码
 * @author CJ
 */
@Controller
open class ChangePasswordController(
    @Nullable
    private val passwordEncoder: PasswordEncoder?,
    @Nullable
    private val passwordChangeHelpService: PasswordChangeHelpService?
) {
    @PostMapping("/passwordChanger")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    open fun changePassword(@AuthenticationPrincipal login: UserDetails, @RequestBody request: Map<String, String>) {
        if (passwordChangeHelpService == null || passwordEncoder == null)
            throw IllegalStateException("并未没有提供 PasswordChangeHelpService或者PasswordEncoder ")
        runAsRoot {
            val principal = passwordChangeHelpService.refreshPrincipal(login)
            try {
                val original = request.getValue("originalPassword")
                if (!passwordEncoder.matches(original, principal.password))
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST)

                val password = request.getValue("password")
                passwordChangeHelpService.updateEncodedPassword(principal, passwordEncoder.encode(password))
            } catch (e: IllegalArgumentException) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST)
            } catch (e: IllegalStateException) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST)
            }
        }

    }
}