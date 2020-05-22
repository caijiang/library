package me.jiangcai.common.wechat.event

import me.jiangcai.common.wechat.entity.WechatUser

/**
 * 微信用户信息修改时
 * @author CJ
 */
data class WechatUserUpdatedEvent(
    val user: WechatUser
)