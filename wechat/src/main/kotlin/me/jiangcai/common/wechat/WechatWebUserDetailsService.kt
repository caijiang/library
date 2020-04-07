package me.jiangcai.common.wechat

import me.jiangcai.common.wechat.entity.WechatUser
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException

/**
 * @author CJ
 */
interface WechatWebUserDetailsService {
    /**
     * 微信身份有了，给一个系统身份
     */
    @Throws(UsernameNotFoundException::class)
    fun findByWechatUser(user: WechatUser): WechatUserAware
}

interface WechatUserAware : UserDetails {
    fun toWechatUser(): WechatUser
}