package me.jiangcai.common.wechat.model

import java.util.*

/**
 * [统一下单](https://github.com/wxpay/WXPay-SDK-Java)
 * @author helloztt
 */
data class MiniUnifiedOrderRequest(
    /**
     * out_trade_no
     * 商户订单号，在商户系统内唯一，8-20位数字或字母，不允许特殊字符
     */
    val orderNumber: String = UUID.randomUUID().toString().replace("-", ""),

    /**
     * 交易类型
     */
    val tradeType: TradeType = TradeType.JSAPI,

    /**
     * 异步回调地址
     */
    val notifyUrl: String,

    /**
     * 小程序支付则是 微信分配的小程序ID
     * appid
     */
    val appId: String,

    /**
     * 微信商户号
     * mch_id
     */
    val merchantId: String,

    /**
     * openid
     */
    val openId: String,

//    /**
//     * 购买商品的标题，最长32位
//     */
//    val subject: String? = null,

    /**
     * 购买商品的描述信息，最长128个字符
     */
    val body: String,

    val detail: String? = null,

//    标价币种	fee_type	否	String(16)	CNY	符合ISO 4217标准的三位字母代码，默认人民币：CNY，详细列表请参见货币类型
    /**
     * 订单总金额，大于0的数字，单位是该币种的货币单位
     * 订单总金额，单位为分
     * total_fee
     */
    val amount: Int,
    /**
     * spbill_create_ip
     */
    val ip: String
//    电子发票入口开放标识	receipt	否	String(8)	Y	Y，传入Y时，支付成功消息和支付详情页将出现开票入口。需要在微信支付商户平台或微信公众平台开通电子发票功能，传此字段才可生效

//    /**
//     * 微信订单号
//     */
//    val transactionId: String? = null,


//    /**
//     * client_ip
//     * 发起支付的客户端IP
//     */
//    val clientIpAddress: String? = null,


//    /**
//     * 可选的订单备注，限制300个字符内
//     */
//    val description: String? = null,
    // time_expire	Long	false	订单失效时间，13位Unix时间戳，默认1小时，微信公众号支付对其的限制为3分钟
    // currency	String	false	三位ISO货币代码，只支持人民币cny，默认cny
//    /**
//     * 可选的用户自定义元数据
//     */
//    val metadata: Map<String, *>? = null
)