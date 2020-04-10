package me.jiangcai.common.wechat.model

/**
 * @author helloztt
 */
enum class TradeType {
    /**
     * 公众号支付
     * 小程序
     */
    JSAPI,

    /**
     * 原生扫码支付
     */
    NATIVE,

    /**
     * app支付
     */
    APP
}
