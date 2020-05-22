package me.jiangcai.common.wechat.entity

/**
 * 退款来源
 * @author CJ
 */
@Suppress("unused")
enum class RefundAccountType {
    /**
     * 余额
     */
    REFUND_SOURCE_RECHARGE_FUNDS,

    /**
     * 未结算
     */
    REFUND_SOURCE_UNSETTLED_FUNDS
}