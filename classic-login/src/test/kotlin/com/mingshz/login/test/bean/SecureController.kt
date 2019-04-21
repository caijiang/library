package com.mingshz.login.test.bean

import com.mingshz.login.test.entity.User
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

/**
 * @author CJ
 */
@Controller
class SecureController {
    @GetMapping("/mine")
    @ResponseBody
    fun mine(@AuthenticationPrincipal user: User): String? {
        return user.username
    }
}