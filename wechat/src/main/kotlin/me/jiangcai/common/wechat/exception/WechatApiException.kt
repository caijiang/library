package me.jiangcai.common.wechat.exception

/**
 * @author CJ
 */
class WechatApiException(
    override val message: String,
    val code: Int
) : Exception()