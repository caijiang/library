package me.jiangcai.common.wechat

import me.jiangcai.common.wechat.entity.WechatUser
import org.springframework.transaction.annotation.Transactional
import java.awt.image.BufferedImage

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

    /**
     * 小程序提交的加密数据，如果通过校验，应该将其更新到持久层中，并且返回新的数据
     * @param user 可能是老旧的数据
     */
    @Transactional
    fun miniDecryptData(user: WechatUser, encryptedData: String, iv: String): WechatUser

    /**
     * https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/qr-code/wxacode.getUnlimited.html
     */
    @Transactional
    fun miniGetUnlimitedQRCode(
        authorization: WechatAccountAuthorization,
        requestParams: Map<String, String>
    ): BufferedImage

}