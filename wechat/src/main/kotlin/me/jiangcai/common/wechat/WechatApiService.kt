package me.jiangcai.common.wechat

import me.jiangcai.common.wechat.entity.WechatPayAccount
import me.jiangcai.common.wechat.entity.WechatPayOrder
import me.jiangcai.common.wechat.entity.WechatUser
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import javax.servlet.http.HttpServletRequest

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
     * 创建小程序支付订单
     * @param request 发起的请求
     * @param account 微信商户
     * @param user 微信用户
     * @param order 被支付的订单
     * @param orderAmount 支付金额，可选
     */
    @Transactional
    fun createUnifiedOrderForMini(
        request: HttpServletRequest?,
        account: WechatPayAccount,
        user: WechatUser,
        order: PayableOrder,
        orderAmount: BigDecimal = order.getOrderDueAmount()
    ): WechatPayOrder

    /**
     * https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=7_7&index=5#
     * @param order 需要支付的订单
     * @return 支持吊起小程序支付的数据
     */
    fun payForMini(order: WechatPayOrder): Map<String, String>

    /**
     * 收到微信支付的异步通知
     */
//    @Transactional
    fun paymentNotify(account: WechatPayAccount, appId: String, data: Map<String, Any?>)

    /**
     * 模拟支付成功
     * 只有在 [WechatSpringConfig.payMockProfile] 下才可以用
     */
    fun mockPayOrderSuccess(wechatPayOrder: WechatPayOrder)
}