package me.jiangcai.common.wechat.controller

import me.jiangcai.common.ext.help.runAsRoot
import me.jiangcai.common.wechat.WechatApiService
import me.jiangcai.common.wechat.WechatPayApiService
import me.jiangcai.common.wechat.WechatUserAware
import me.jiangcai.common.wechat.repository.WechatPayAccountRepository
import me.jiangcai.common.wechat.requestWechatAccountAuthorization
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletRequest

/**
 * @author CJ
 */
@Controller
class WechatController(
    @Autowired
    private val wechatPayApiService: WechatPayApiService,
    @Autowired
    private val wechatPayAccountRepository: WechatPayAccountRepository,
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
        @AuthenticationPrincipal details: Any,
        @RequestParam encryptedData: String, @RequestParam iv: String
    ): Any {
        return wechatApiService.miniDecryptData((details as WechatUserAware).toWechatUser(), encryptedData, iv)
    }

    @Suppress("MVCPathVariableInspection")
    @PostMapping("\${me.jiangcai.wechat.payNotifyUri:/wechat/paymentNotify}")
    fun paymentNotify(@RequestBody data: Map<String, Any?>): ResponseEntity<String> {
        // 寻找何时的 account
        runAsRoot {
            val appId = data["appid"].toString()
            val account = wechatPayAccountRepository.findByIdOrNull(data["mch_id"].toString())
                ?: throw IllegalArgumentException("not merchant find")

            wechatPayApiService.paymentNotify(account, appId, data)
        }
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_XML)
            .body("<xml><return_code><![CDATA[SUCCESS]]></return_code></xml>")
    }

    @Suppress("MVCPathVariableInspection")
    @PostMapping("\${me.jiangcai.wechat.payNotifyUri:/wechat/paymentNotify}/refund")
    fun refundNotify(@RequestBody data: Map<String, Any?>): ResponseEntity<String> {
        // 寻找何时的 account
        runAsRoot {
            val appId = data["appid"].toString()
            val account = wechatPayAccountRepository.findByIdOrNull(data["mch_id"].toString())
                ?: throw IllegalArgumentException("not merchant find")

            wechatPayApiService.refundNotify(account, appId, data)
        }
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_XML)
            .body("<xml><return_code><![CDATA[SUCCESS]]></return_code></xml>")
    }

}