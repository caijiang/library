package com.mingshz.login.wechat.entity

import com.mingshz.login.entity.Login
import me.jiangcai.wx.standard.entity.StandardWeixinUser
import me.jiangcai.wx.standard.entity.support.AppIdOpenID
import javax.persistence.*

/**
 * @author CJ
 */
@Entity
@IdClass(AppIdOpenID::class)
data class WechatLogin(
    @OneToOne
    @Id
    var wechat: StandardWeixinUser? = null,
    @ManyToOne
    var login: Login? = null
)