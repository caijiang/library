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
import org.springframework.web.bind.annotation.*
import javax.imageio.ImageIO
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

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

    // png
    @GetMapping("\${me.jiangcai.wechat.miniGetUnlimitedQRCode.uri:/wechat/min/unlimitedQRCode}")
    fun miniGetUnlimitedQRCode(
        @RequestParam requestParams: Map<String, String>,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        val image = wechatApiService.miniGetUnlimitedQRCode(
            applicationContext.requestWechatAccountAuthorization(request),
            requestParams
        )
        val names = ImageIO.getWriterFormatNames()
        val type = request.getHeader("Accept")?.removePrefix("image/")?.let { t ->
            // 做一次校验 看它是否符合图片标准
            if (names.map { it.toUpperCase() }.contains(t.toUpperCase())) t
            else null
        } ?: "png"

        response.setHeader("Content-Type", "image/$type")
        response.outputStream.use {
            ImageIO.write(image, type, it)
            it.flush()
        }
//        response.sendError(200)
    }

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