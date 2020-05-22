package me.jiangcai.common.wechat.event

import me.jiangcai.common.wechat.entity.WechatRefundOrder

/**
 * @author CJ
 */
data class WechatRefundSuccessEvent(
    val order: WechatRefundOrder, val appId: String
)