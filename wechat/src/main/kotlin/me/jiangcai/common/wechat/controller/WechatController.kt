package me.jiangcai.common.wechat.controller

import me.jiangcai.common.wechat.WechatApiService
import me.jiangcai.common.wechat.WechatUserAware
import me.jiangcai.common.wechat.requestWechatAccountAuthorization
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.security.core.annotation.AuthenticationPrincipal
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
    @PostMapping("\${me.jiangcai.wechat.webSignature.uri:/webSignature}")
    @ResponseBody
    fun signature(@RequestParam url: String, request: HttpServletRequest): Any {
        return wechatApiService.signature(applicationContext.requestWechatAccountAuthorization(request), url)
    }

    @Suppress("MVCPathVariableInspection")
    @PostMapping("\${me.jiangcai.wechat.miniDecryptDataForUserInfo.uri:/wechatMiniDecryptDataForUserInfo}")
    @ResponseBody
    fun miniDecryptDataForUserInfo(
        @AuthenticationPrincipal details: WechatUserAware,
        @RequestParam encryptedData: String, @RequestParam iv: String
    ): Any {
        return wechatApiService.miniDecryptData(details.toWechatUser(), encryptedData, iv)
    }

}