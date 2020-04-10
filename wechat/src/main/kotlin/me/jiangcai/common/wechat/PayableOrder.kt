package me.jiangcai.common.wechat

import java.math.BigDecimal

/**
 * 可被支付的订单
 * @author CJ
 */
interface PayableOrder {

    /**
     * @return 通常应该是 xx-id 的方式描述这个订单的有效识别符
     */
    fun getOrderToPayOrderIdentify(): String

    /**
     * 指定货币目前仅指人民币
     *
     * @return 订单总应付款金额，单位指定货币元
     */
    fun getOrderDueAmount(): BigDecimal

    /**
     * @return 订单商品名称
     */
    fun getOrderProductName(): String

    /**
     * @return 详细的商品描述
     */
    fun getOrderBody(): String
}