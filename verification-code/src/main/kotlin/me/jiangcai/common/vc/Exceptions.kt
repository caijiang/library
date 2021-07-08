package me.jiangcai.common.vc


/**
 * 验证码发送过于频繁
 *
 * @author CJ
 * @since 1.2
 */
class FrequentlySendException : IllegalStateException {
    constructor(s: String?) : super(s) {}
    constructor(message: String?, cause: Throwable?) : super(message, cause) {}
}


/**
 * 无效的验证码
 *
 * @author CJ
 */
class IllegalVerificationCodeException(@Suppress("unused") private val type: VerificationType) :
    RuntimeException("无效的验证码")