package com.mingshz.login.wechat

import me.jiangcai.wx.standard.entity.support.AppIdOpenID
import java.io.Serializable

/**
 * 表示这个用户即将登录
 * @author CJ
 */
data class WechatUserLoginEvent(
    val id: AppIdOpenID
) : Serializable