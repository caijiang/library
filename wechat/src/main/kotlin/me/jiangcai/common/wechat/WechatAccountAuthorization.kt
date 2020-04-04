package me.jiangcai.common.wechat

/**
 * 微信账户授权书
 * @author CJ
 */
data class WechatAccountAuthorization(
    val accountAppId: String?,
    val accountAppSecret: String?,
    val miniAppId: String?,
    val miniAppSecret: String?
)