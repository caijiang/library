package com.mingshz.login.test.beans

import com.mingshz.login.test.MyService
import com.mingshz.login.test.entity.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

/**
 * @author CJ
 */
@Controller
open class SecureControllerImpl(
    @Autowired
    private val myService: MyService
) {
    @GetMapping("/mine")
    @ResponseBody
    fun mine(@AuthenticationPrincipal user: User?): String? {
        return user?.username
    }

    @GetMapping("/advancedMine")
//    @PreAuthorize("hasAnyRole('ROOT')")
    @ResponseBody
    fun advancedMine(@AuthenticationPrincipal user: User?): String? {
        myService.wellDone()
        return user?.username
    }
}