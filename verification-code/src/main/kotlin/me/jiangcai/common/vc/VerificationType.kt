package me.jiangcai.common.vc

import me.jiangcai.lib.notice.Content

/**
 * 验证类型
 *
 * @author CJ
 */
interface VerificationType {
    /**
     * @return 每一种不同的类型都应当提供一个识别服
     */
    fun id(): Int

    /**
     * @return 过期秒数
     */
    fun expireSeconds(): Int {
        return 5 * 60
    }

    /**
     * @return 多少秒内不可重发
     */
    fun protectSeconds(): Int {
        return 60
    }

    /**
     * @return 是否允许多个验证码同时有效
     * @since 1.3
     */
    fun allowMultiple(): Boolean {
        return false
    }

    /**
     * @param code 随机码
     * @return 即将发送给手机的文本内容
     */
    @Deprecated("1.4之后不再使用该方法")
    fun message(code: String?): String?

    /**
     * @return 随机码长度
     */
    fun codeLength(): Int {
        return 4
    }

    /**
     * @param code 随机码
     * @return 即将发送给手机的信息内容
     * @since 1.4
     */
    fun generateContent(code: String): Content
}
