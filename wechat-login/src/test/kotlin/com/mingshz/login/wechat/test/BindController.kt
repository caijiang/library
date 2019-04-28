package com.mingshz.login.wechat.test

import com.mingshz.login.ClassicLoginService
import com.mingshz.login.wechat.WechatLoginService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseStatus
import javax.servlet.http.HttpServletRequest

/**
 * @author CJ
 */
@Controller
class BindController {

    @Autowired
    private lateinit var classicLoginService: ClassicLoginService<*>
    @Autowired
    private lateinit var wechatLoginService: WechatLoginService

    /**
     * 让当前微信绑定到
     */
    @GetMapping("/bind/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun bind(@PathVariable("id") loginId: Long, request: HttpServletRequest) {
        wechatLoginService.assignWechat(
            classicLoginService.findLogin(loginId), request
        )
    }

}