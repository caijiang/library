package me.jiangcai.common.wechat.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import me.jiangcai.common.ext.help.toSimpleString
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne

/**
 * 微信退款订单
 * @author CJ
 */
@Entity
data class WechatRefundOrder(
    @Id
    @Column(length = 32)
    val id: String = UUID.randomUUID().toSimpleString(),
    @JsonIgnore
    @ManyToOne
    val payOrder: WechatPayOrder,
    /**
     * 退款金额
     */
    @Column(scale = 2, precision = 11)
    val amount: BigDecimal,
    /**
     * 退款理由
     */
    @Column(length = 80)
    val reason: String? = null,
    val createTime: LocalDateTime = LocalDateTime.now(),
    /**
     * 请求退款 API 返回结果
     */
    var requestSuccess: Boolean = false,
    @Column(length = 32)
    var requestErrorCode: String? = null,
    @Column(length = 128)
    var requestErrorMessage: String? = null,
    /**
     * 微信退款单号
     */
    @Column(length = 32)
    var refundId: String? = null,
    /**
     * 实际退款金额
     * 退款金额=申请退款金额-非充值代金券退款金额，退款金额<=申请退款金额
     */
    @Column(scale = 2, precision = 11)
    var settlementAmount: BigDecimal? = null,
    var success: Boolean = false,
    var changed: Boolean = false,
    var close: Boolean = false,
    /**
     * 成功退款时间
     */
    var successTime: LocalDateTime? = null,
    /**
     * 退款入户账户
     */
    @Column(length = 64)
    var receiveAccount: String? = null,
    var account: RefundAccountType? = null,
    var sourceType: RefundSourceType? = null
) {

    fun successWith(
        settlementAmount: BigDecimal,
        successTime: LocalDateTime?,
        account: String?,
        accountType: RefundAccountType,
        sourceType: RefundSourceType
    ) {
        close = false
        changed = false
        success = true
        this.settlementAmount = settlementAmount
        this.successTime = successTime
        this.receiveAccount = account
        this.account = accountType
        this.sourceType = sourceType
    }

    fun closeWith() {
        success = false
        changed = false
        close = true
    }

    fun changeWith() {
        success = false
        close = false
        changed = true
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WechatRefundOrder) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "WechatRefundOrder(id='$id', amount=$amount, reason=$reason, createTime=$createTime, requestSuccess=$requestSuccess, requestErrorCode=$requestErrorCode, requestErrorMessage=$requestErrorMessage, refundId=$refundId, settlementAmount=$settlementAmount, success=$success, changed=$changed, close=$close, successTime=$successTime, receiveAccount=$receiveAccount, account=$account, sourceType=$sourceType)"
    }
}