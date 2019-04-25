package com.mingshz.login.test

import com.mingshz.login.test.entity.User
import org.springframework.security.access.annotation.Secured
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

/**
 * @author CJ
 */
interface SecureController {
    @PreAuthorize("!isAnonymous()")
    @Secured
    @GetMapping("/mine")
    @ResponseBody
    fun mine(@AuthenticationPrincipal user: User?): String?
}