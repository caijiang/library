package me.jiangcai.common.wechat.event

import me.jiangcai.common.wechat.entity.WechatPayOrder

/**
 * 微信支付成功通知
 * @author CJ
 */
data class WechatPaySuccessEvent(
    val order: WechatPayOrder,
    val appId: String
)