package me.jiangcai.common.wechat

import me.jiangcai.common.wechat.entity.*
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import javax.servlet.http.HttpServletRequest

/**
 * 微信支付相关 API
 * @author CJ
 */
interface WechatPayApiService {

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
     * 申请退款
     * 如果上一笔订单没有退款成功，那么系统会拒绝。
     * @param order 原支付订单
     * @param amount 退款金额；单位：元
     * @param reason 退款原因（不超过80字符）
     */
    @Transactional
    fun refundPayForMini(
        order: WechatPayOrder, amount: BigDecimal, reason: String? = null
        , account: RefundAccountType = RefundAccountType.REFUND_SOURCE_UNSETTLED_FUNDS
    ): WechatRefundOrder

    @Transactional
    fun refundPayForMini(
        order: WechatRefundOrder,
        account: RefundAccountType = RefundAccountType.REFUND_SOURCE_UNSETTLED_FUNDS
    ): WechatRefundOrder

    /**
     * 收到微信退款的异步通知
     */
    fun refundNotify(account: WechatPayAccount, appId: String, data: Map<String, Any?>)

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

    /**
     * 模拟退款成功
     * 只有在 [WechatSpringConfig.payMockProfile] 下才可以用
     */
    fun mockRefundOrderSuccess(order: WechatRefundOrder)
}