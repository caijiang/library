package me.jiangcai.common.wechat.entity

import com.fasterxml.jackson.annotation.JsonProperty
import me.jiangcai.common.ext.sumByBigDecimal
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Expression
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
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
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

//    /**
//     * SUCCESS—支付成功
//     * REFUND—转入退款
//     * NOTPAY—未支付
//     * CLOSED—已关闭
//     * REVOKED—已撤销（刷卡支付）
//     * USERPAYING--用户支付中
//     * PAYERROR--支付失败(其他原因，如银行返回失败)
//     */
//    @Column(length = 15)
//    var orderStatus: String? = null,
    var orderSuccess: Boolean? = null,

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
    @OneToMany(mappedBy = "payOrder", orphanRemoval = true)
    val refundOrders: List<WechatRefundOrder> = emptyList()
//    /**
//     * 用户撤销时间
//     */
//    val revokeTime: LocalDateTime? = null,
//    /**
//     * 退款时间
//     */
//    val refundTime: LocalDateTime? = null

) {
    companion object {
        /**
         * @return 成功支付的条件
         */
        fun toOrdinalSuccessPay(cb: CriteriaBuilder, orderForm: From<*, WechatPayOrder>): Predicate {
            // 支付成功
            return cb.and(
                cb.isTrue(orderForm.get(WechatPayOrder_.orderSuccess)),
//                cb.equal(orderForm.get(WechatPayOrder_.orderSuccess), true),
                cb.isNotNull(orderForm.get(WechatPayOrder_.payTime)),
                cb.isNotNull(orderForm.get(WechatPayOrder_.payTransactionId))
            )
        }

        fun toRefundAmount(cb: CriteriaBuilder, orderForm: From<*, WechatPayOrder>): Expression<BigDecimal> {
            val allRefund = orderForm.join(WechatPayOrder_.refundOrders)
            // 只取值 success
            val value = cb.selectCase<BigDecimal>()
                .`when`(cb.isTrue(allRefund[WechatRefundOrder_.requestSuccess]), allRefund[WechatRefundOrder_.amount])
                .otherwise(cb.literal(BigDecimal.ZERO))
            return cb.coalesce(cb.sum(value), cb.literal(BigDecimal.ZERO))
        }

        fun toRefundSuccessAmount(cb: CriteriaBuilder, orderForm: From<*, WechatPayOrder>): Expression<BigDecimal> {
            val allRefund = orderForm.join(WechatPayOrder_.refundOrders)
            // 只取值 success
            val value = cb.selectCase<BigDecimal>()
                .`when`(
                    cb.and(
                        cb.isTrue(allRefund[WechatRefundOrder_.requestSuccess])
                        , cb.isTrue(allRefund[WechatRefundOrder_.success])
                    ), allRefund[WechatRefundOrder_.amount]
                )
                .otherwise(cb.literal(BigDecimal.ZERO))
            return cb.coalesce(cb.sum(value), cb.literal(BigDecimal.ZERO))
        }
    }

    /**
     * 原始订单成功支付
     */
    val ordinalSuccessPay: Boolean
        get() = orderSuccess == true && payTime != null && payTransactionId != null

    /**
     * 已申请的退款金额
     */
    val refundAmount: BigDecimal
        get() = refundOrders.filter { it.requestSuccess }.sumByBigDecimal { it.amount }

    /**
     * 成功退款总额
     */
    val refundSuccessAmount: BigDecimal
        get() = refundOrders.filter { it.requestSuccess && it.success }.sumByBigDecimal { it.amount }

    /**
     * 成功支付
     */
    fun payWith(id: String, time: LocalDateTime) {
//        orderStatus = "SUCCESS"
        orderSuccess = true
        payTime = time
        payTransactionId = id
    }

    fun failed() {
        orderSuccess = false
    }
}