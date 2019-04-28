package com.mingshz.login.wechat.filter

import com.mingshz.login.entity.Login
import me.jiangcai.wx.standard.entity.support.AppIdOpenID
import org.springframework.security.authentication.AbstractAuthenticationToken
import java.util.*

/**
 * @author CJ
 */
class WechatAuthenticationToken(
    val id: AppIdOpenID,
    val login: Login? = null
) :
    AbstractAuthenticationToken(login?.authorities ?: Collections.emptyList()) {

    override fun getCredentials(): Any = id

    override fun getPrincipal(): Any? = login

}