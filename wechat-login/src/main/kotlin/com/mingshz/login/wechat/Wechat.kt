package com.mingshz.login.wechat

import me.jiangcai.wx.model.WeixinUserDetail
import me.jiangcai.wx.standard.entity.support.AppIdOpenID

/**
 * @return 微信id
 */
fun WeixinUserDetail.toWechatId(): AppIdOpenID {
    return AppIdOpenID(this.appId, this.openId)
}