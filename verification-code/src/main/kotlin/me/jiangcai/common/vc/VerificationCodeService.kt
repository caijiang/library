package me.jiangcai.common.vc

import me.jiangcai.lib.notice.NoticeSender
import org.springframework.transaction.annotation.Transactional
import java.io.IOException


/**
 * 验证码服务
 *
 * @author CJ
 */
interface VerificationCodeService {
    /**
     * 验证
     *
     * @param mobile 手机号码
     * @param code   校验该验证码是否正确
     * @param type   验证码类型
     * @throws IllegalVerificationCodeException 如果无效
     */
    @Transactional(readOnly = true)
    @Throws(IllegalVerificationCodeException::class)
    fun verify(mobile: String, code: String, type: VerificationType)

    /**
     * 发送验证码
     *
     * @param mobile 手机号码
     * @param type   验证码类型
     * @throws IOException             如果发送失败
     * @throws FrequentlySendException 如果重复发送
     */
    @Transactional
    @Throws(IOException::class)
    fun sendCode(mobile: String, type: VerificationType)

    /**
     * 发送验证码
     *
     * @param sender 发送者
     * @param mobile 手机号码
     * @param type   验证码类型
     * @throws IOException             如果发送失败
     * @throws FrequentlySendException 如果重复发送
     */
    @Transactional
    @Throws(IOException::class)
    fun sendCode(sender: NoticeSender?, mobile: String, type: VerificationType)
}
