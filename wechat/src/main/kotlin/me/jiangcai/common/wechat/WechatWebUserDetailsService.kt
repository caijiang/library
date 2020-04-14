package me.jiangcai.common.wechat

import me.jiangcai.common.wechat.entity.WechatUser
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import javax.servlet.http.HttpServletRequest

/**
 * @author CJ
 */
interface WechatWebUserDetailsService {
    /**
     * 微信身份有了，给一个系统身份
     * @param user 微信身份
     * @param request 请求身份时的原始 servlet请求
     */
    @Throws(UsernameNotFoundException::class)
    fun findByWechatUser(
        user: WechatUser,
        request: HttpServletRequest? = null
    ): WechatUserAware
}

interface WechatUserAware : UserDetails {
    fun toWechatUser(): WechatUser
}