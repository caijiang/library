package me.jiangcai.common.vc.service

import me.jiangcai.common.vc.VerificationType
import me.jiangcai.lib.notice.Content
import me.jiangcai.lib.notice.NoticeSender
import org.apache.commons.logging.LogFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

/**
 * @author CJ
 */
@Profile("(staging | development | test) & !vcSendTest")
@Service
class TestVerificationCodeService : AbstractVerificationCodeService() {

    private val code = "1234567890"
    private val log = LogFactory.getLog(TestVerificationCodeService::class.java)
    override fun send(sender: NoticeSender?, mobile: String, content: Content) {
        log.info("发送验证码 $content to $mobile")
    }

    override fun generateCode(mobile: String, type: VerificationType): String {
        return code.substring(0, type.codeLength())
    }

}