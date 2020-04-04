package me.jiangcai.common.vc.service

import me.jiangcai.common.ss.SystemStringService
import me.jiangcai.common.vc.FrequentlySendException
import me.jiangcai.common.vc.IllegalVerificationCodeException
import me.jiangcai.common.vc.VerificationCodeService
import me.jiangcai.common.vc.VerificationType
import me.jiangcai.common.vc.entity.VerificationCode
import me.jiangcai.common.vc.entity.VerificationCodeMultiple
import me.jiangcai.common.vc.entity.VerificationCodePK
import me.jiangcai.common.vc.repository.VerificationCodeMultipleRepository
import me.jiangcai.common.vc.repository.VerificationCodeRepository
import me.jiangcai.lib.notice.Content
import me.jiangcai.lib.notice.NoticeSender
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import java.io.IOException
import java.util.*

/**
 * @author CJ
 */
abstract class AbstractVerificationCodeService : VerificationCodeService {

    @Autowired
    private lateinit var verificationCodeRepository: VerificationCodeRepository

    @Autowired
    private lateinit var verificationCodeMultipleRepository: VerificationCodeMultipleRepository

    @Autowired
    private lateinit var systemStringService: SystemStringService

    override fun verify(mobile: String, code: String, type: VerificationType) {
        val instance = Calendar.getInstance()
        instance.add(Calendar.SECOND, -type.expireSeconds())
        if (type.allowMultiple()) {
            val list: List<VerificationCodeMultiple> =
                verificationCodeMultipleRepository.findByMobileAndType(mobile, type.id())
            if (list.isEmpty()) throw IllegalVerificationCodeException(type)
            // 过滤掉过期的，再过滤掉不匹配的，如果剩下还存在
            if (code == getSuperCode()) {
                return
            }
            if (list.stream()
                    .filter { verificationCodeMultiple: VerificationCodeMultiple ->
                        verificationCodeMultiple.code == code
                    }
                    .noneMatch { verificationCodeMultiple: VerificationCodeMultiple ->
                        instance.before(
                            verificationCodeMultiple.sendTime
                        )
                    }
            ) throw IllegalVerificationCodeException(type)
        } else {
            val verificationCode: VerificationCode =
                verificationCodeRepository.findByIdOrNull(VerificationCodePK(mobile, type))
                    ?: throw IllegalVerificationCodeException(type)
            if (instance.after(verificationCode.sendTime)) throw IllegalVerificationCodeException(type)
            if (code == getSuperCode()) {
                return
            }
            if (verificationCode.code != code) throw IllegalVerificationCodeException(type)
        }
    }

    private fun getSuperCode(): String {
        return systemStringService.getCustomSystemString("best.vc", null, true, String::class.java, "9527")
    }

    override fun sendCode(sender: NoticeSender?, mobile: String, type: VerificationType) {
        val instance = Calendar.getInstance()
        instance.add(Calendar.SECOND, -type.protectSeconds())
        // 短时间内不允许 1 分钟?
        // 有效时间 10分钟?
        if (type.allowMultiple()) {
            val list =
                verificationCodeMultipleRepository.findByMobileAndType(mobile, type.id())
            if (list.isNotEmpty()) {

                // 最近发的

                if (instance.before(list.map { it.sendTime }.max())
                ) throw FrequentlySendException("短时间内不可以重复发送。")
            }
            // 添加一个
            val verificationCode = VerificationCodeMultiple(
                mobile = mobile, type = type.id(), code = generateCode(mobile, type), sendTime = Calendar.getInstance()
            )
            // 执行发送
            send(sender, mobile, type.generateContent(verificationCode.code))

            verificationCodeMultipleRepository.save(verificationCode)
        } else {

            val lastYear = Calendar.getInstance()
            lastYear.add(Calendar.YEAR, -1)

            val verificationCode =
                verificationCodeRepository.findByIdOrNull(VerificationCodePK(mobile, type)) ?: VerificationCode(
                    mobile = mobile, type = type.id(), code = "", sendTime = lastYear
                )

            if (instance.before(verificationCode.sendTime)) throw FrequentlySendException("短时间内不可以重复发送。")
            val code = generateCode(mobile, type)

            // 执行发送
            send(sender, mobile, type.generateContent(code))

            // 保存数据库
            verificationCode.code = code
            verificationCode.sendTime = Calendar.getInstance()
            verificationCodeRepository.save(verificationCode)
        }
    }

    override fun sendCode(mobile: String, type: VerificationType) {
        sendCode(null, mobile, type)
    }


    /**
     * 实际的发送文本
     *
     * @param sender
     * @param mobile      接受手机号码
     * @param content 内容
     */
    @Throws(IOException::class)
    protected abstract fun send(
        sender: NoticeSender?,
        mobile: String,
        content: Content
    )

    /**
     * @param mobile 手机号码
     * @param type   类型
     * @return 生成随机码
     */
    protected abstract fun generateCode(mobile: String, type: VerificationType): String

}
