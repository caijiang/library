package me.jiangcai.common.wechat.entity

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.From
import javax.persistence.criteria.Predicate

/**
 * 微信支付订单
 * @author CJ
 */
@Entity
data class WechatPayOrder(
    @Id
    @Column(length = 32)
    val id: String = UUID.randomUUID().toString().replace("-", ""),
    /**
     * 为了支付什么来着？
     */
    @Column(length = 40)
    val toPayOrderIdentify: String,
    /**
     * 预支付交易会话标识
     */
    @Column(length = 64)
    val prepayId: String,
    /**
     * 微信支付商户
     */
    @ManyToOne
    val account: WechatPayAccount,
    @ManyToOne
    val user: WechatUser,

    /**
     * 应付款额，单位元
     */
    @Column(scale = 2, precision = 11)
    val amount: BigDecimal,

    /**
     * 一段脚本可以引导支付
     */
    @Lob
    var javascriptToPay: String? = null,

    /**
     * 二维码链接
     */
    @Column(length = 64)
    var codeUrl: String? = null,

    /**
     * SUCCESS—支付成功
     * REFUND—转入退款
     * NOTPAY—未支付
     * CLOSED—已关闭
     * REVOKED—已撤销（刷卡支付）
     * USERPAYING--用户支付中
     * PAYERROR--支付失败(其他原因，如银行返回失败)
     */
    @Column(length = 15)
    var orderStatus: String? = null,

    /**
     * 支付成功的跳转地址
     */
    @Column
    var redirectUrl: String? = null,
    val createTime: LocalDateTime = LocalDateTime.now(),
    /**
     * 支付时间
     */
    var payTime: LocalDateTime? = null,
    /**
     * 微信支付订单号
     */
    @Column(length = 32)
    var payTransactionId: String? = null,
    /**
     * 用户撤销时间
     */
    val revokeTime: LocalDateTime? = null,
    /**
     * 退款时间
     */
    val refundTime: LocalDateTime? = null

) {
    companion object {
        /**
         * @return 成功支付的条件
         */
        @Suppress("unused")
        fun toSuccessPayOrder(cb: CriteriaBuilder, orderForm: From<*, WechatPayOrder>): Predicate {
            // 支付成功，而且没有被退款
            return cb.and(
                cb.equal(orderForm.get(WechatPayOrder_.orderStatus), "SUCCESS"),
                cb.isNotNull(orderForm.get(WechatPayOrder_.payTime)),
                cb.isNotNull(orderForm.get(WechatPayOrder_.payTransactionId))
            )
        }
    }

    /**
     * 成功支付
     */
    fun payWith(id: String, time: LocalDateTime) {
        orderStatus = "SUCCESS"
        payTime = time
        payTransactionId = id
    }
}