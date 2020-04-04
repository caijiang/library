package me.jiangcai.common.vc.service

import me.jiangcai.common.vc.FrequentlySendException
import me.jiangcai.common.vc.VerificationType
import me.jiangcai.lib.notice.Content
import me.jiangcai.lib.notice.NoticeSender
import me.jiangcai.lib.notice.NoticeService
import me.jiangcai.lib.notice.To
import me.jiangcai.lib.notice.email.EmailAddress
import me.jiangcai.lib.notice.exception.BadToException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.util.*

/**
 * @author CJ
 */
@Profile("!staging & !development &!test")
@Service
class VerificationCodeServiceImpl(
    @Suppress("SpringJavaInjectionPointsAutowiringInspection") @Autowired
    private val noticeService: NoticeService
) : AbstractVerificationCodeService() {

    private val random = Random()
    private lateinit var charList: CharArray

    init {
        val symbol = StringBuilder(10)
        var character = '0'
        while (character <= '9') {
            symbol.append(character)
            character++
        }
        charList = symbol.toString().toCharArray()
    }

    override fun send(sender: NoticeSender?, mobile: String, content: Content) {
        val to: To = object : To {
            override fun mobilePhone(): String {
                return mobile
            }

            override fun emailTo(): Set<EmailAddress> {
                return emptySet()
            }
        }

        try {
            if (sender != null) {
                noticeService.send(sender, to, content)
            } else {
                noticeService.send(to, content)
            }
        } catch (ex: BadToException) {
            throw FrequentlySendException("短时间内不可以重复发送。", ex)
        }
    }

    /**
     * Creates a new random [java.lang.String].
     *
     * @return A random [java.lang.String] of the given length for this instance.
     */
    fun nextString(length: Int): String {
        val buffer = CharArray(length)
        for (index in 0 until length) {
            buffer[index] =
                charList[random.nextInt(charList.size)]
        }
        return String(buffer)
    }

    override fun generateCode(mobile: String, type: VerificationType): String {
        return nextString(type.codeLength())
    }
}