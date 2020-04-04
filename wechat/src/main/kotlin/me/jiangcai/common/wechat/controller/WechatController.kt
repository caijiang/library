package me.jiangcai.common.wechat.controller

import me.jiangcai.common.wechat.WechatApiService
import me.jiangcai.common.wechat.requestWechatAccountAuthorization
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletRequest

/**
 * @author CJ
 */
@Controller
class WechatController(
    @Autowired
    private val wechatApiService: WechatApiService,
    @Autowired
    private val applicationContext: ApplicationContext
) {

    @Suppress("MVCPathVariableInspection")
    @PostMapping("\${me.jiangcai.weixin.webSignature.uri:/webSignature}")
    @ResponseBody
    fun signature(@RequestParam url: String, request: HttpServletRequest): Any {
        return wechatApiService.signature(applicationContext.requestWechatAccountAuthorization(request), url)
    }

}