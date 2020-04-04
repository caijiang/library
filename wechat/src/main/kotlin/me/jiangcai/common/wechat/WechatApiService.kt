package me.jiangcai.common.wechat

import me.jiangcai.common.wechat.entity.WechatUser
import org.springframework.transaction.annotation.Transactional

/**
 * @author CJ
 */
interface WechatApiService {
    /**
     * 微信网页 JS 授权
     */
    fun signature(authorization: WechatAccountAuthorization, url: String): Map<String, Any>

    /**
     * 根据个人授权码获取该用户信息
     * 比较特别的是，这个无任何要求
     */
    @Transactional
    fun queryUserViaAuthorizationCode(authorization: WechatAccountAuthorization, code: String): WechatUser

    /**
     *
     * https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/login.html
     * 根据小程序个人授权码获取该用户信息
     * 比较特别的是，这个无任何要求
     */
    @Transactional
    fun queryUserViaMiniAuthorizationCode(authorization: WechatAccountAuthorization, code: String): WechatUser
}