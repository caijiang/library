package me.jiangcai.common.wechat.service

import me.jiangcai.common.ext.help.toSimpleString
import me.jiangcai.common.ext.mvc.clientIpAddress
import me.jiangcai.common.ext.mvc.contextUrl
import me.jiangcai.common.wechat.PayableOrder
import me.jiangcai.common.wechat.WechatSpringConfig
import me.jiangcai.common.wechat.entity.WechatPayAccount
import me.jiangcai.common.wechat.entity.WechatPayOrder
import me.jiangcai.common.wechat.entity.WechatUser
import me.jiangcai.common.wechat.event.WechatPaySuccessEvent
import me.jiangcai.common.wechat.model.TradeType
import me.jiangcai.common.wechat.util.WechatPayResponseHandler
import org.apache.commons.codec.binary.Hex
import org.apache.http.client.entity.EntityBuilder
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.springframework.transaction.support.TransactionTemplate
import java.math.BigDecimal
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.servlet.http.HttpServletRequest


// 微信支付相关
private val wechatDateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")

internal fun WechatApiServiceImpl.mockPayOrderSuccessImpl(order: WechatPayOrder) {
    if (!environment.acceptsProfiles(WechatSpringConfig.payMockProfile)) {
        log.warn("无法在非模拟环境下使用模拟支付")
        return
    }
    val o = TransactionTemplate(platformTransactionManager)
        .execute {
            order.payWith(
                UUID.randomUUID().toSimpleString(), LocalDateTime.now()
            )
            wechatPayOrderRepository.save(order)
        }

    applicationContext.publishEvent(
        WechatPaySuccessEvent(
            o!!, o.user.appId
        )
    )
}

internal fun payForMiniImpl(order: WechatPayOrder): Map<String, String> {
    val map = mapOf(
        "appId" to order.user.appId,
        "timeStamp" to "${System.currentTimeMillis() / 100}",
        "nonceStr" to UUID.randomUUID().toSimpleString(),
        "package" to "prepay_id=${order.prepayId}",
        "signType" to "MD5"
    ).toMutableMap()

    map["paySign"] = signValue(map, order.account)
    return map
}

internal fun WechatApiServiceImpl.paymentNotifyImpl(
    account: WechatPayAccount,
    appId: String,
    data: Map<String, Any?>
) {
    // https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=9_7&index=8
    if (data["return_code"] != "SUCCESS")
        return
    // 验签
    if (!environment.acceptsProfiles(WechatSpringConfig.techTestProfile))
        verifySignMap(account, data)

    // 成功支付的表示 result_code
    // transaction_id out_trade_no total_fee
    if (data["result_code"] == "SUCCESS") {
        // 成功支付
        val order = TransactionTemplate(platformTransactionManager)
            .execute {
                val order = wechatPayOrderRepository.getOne(data["out_trade_no"].toString())
                if (order.amount.movePointRight(2)
                        .intValueExact() != BigDecimal(data["total_fee"].toString()).intValueExact()
                ) {
                    log.warn("wechatPay notify with illegal amount: our side:${order.amount} with notify side:${data["total_fee"]}")
                    throw IllegalStateException("Illegal total_fee")
                }
                order.payWith(
                    data["transaction_id"].toString(), LocalDateTime.from(
                        wechatDateTimeFormatter.parse(data["time_end"].toString())
                    )
                )
                wechatPayOrderRepository.save(order)
            }


        applicationContext.publishEvent(
            WechatPaySuccessEvent(
                order!!, appId
            )
        )
    }

}


internal fun WechatApiServiceImpl.createUnifiedOrderForMiniImpl(
    request: HttpServletRequest,
    account: WechatPayAccount,
    user: WechatUser,
    order: PayableOrder,
    orderAmount: BigDecimal
): WechatPayOrder {

    val url = account.paymentNotifyUrlPrefix ?: request.contextUrl()
    val requestId = UUID.randomUUID().toSimpleString()
    val data = mapOf(
        "appid" to user.appId,
        "mch_id" to account.merchantId,
//        设备号	device_info	否	String(32)	013467007045764	自定义参数，可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB"
        "body" to order.getOrderProductName(),
        "detail" to order.getOrderBody(),
//        附加数据	attach	否	String(127)	深圳分店	附加数据，在查询API和支付通知中原样返回，可作为自定义参数使用。
        "out_trade_no" to requestId,
//        标价币种	fee_type	否	String(16)	CNY	符合ISO 4217标准的三位字母代码，默认人民币：CNY，详
        "total_fee" to orderAmount.movePointRight(2).intValueExact(),
        "spbill_create_ip" to request.clientIpAddress(),
//        交易起始时间	time_start	否	String(14)	20091225091010	订单生成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010。其他详见时间规则
//                交易结束时间	time_expire	否	String(14)	20091227091010
//                订单失效时间，格式为yyyyMMddHHmmss，如2009年12月27日9点10分10秒表示为20091227091010。订单失效时间是针对订单号而言的，由于在请求支付的时候有一个必传参数prepay_id只有两小时的有效期，所以在重入时间超过2小时的时候需要重新请求下单接口获取新的prepay_id。其他详见时间规则
//
//                建议：最短失效时间间隔大于1分钟
//
//            订单优惠标记	goods_tag	否	String(32)	WXG	订单优惠标记，使用代金券或立减优惠功能时需要的参数，说明详见代金券或立减优惠
        "notify_url" to "$url${payNotifyUri}",
        "trade_type" to TradeType.JSAPI.name,
//        商品ID	product_id	否	String(32)	12235413214070356458058	trade_type=NATIVE时，此参数必传。此参数为二维码中包含的商品ID，商户自行定义。
//                指定支付方式	limit_pay	否	String(32)	no_credit	上传此参数no_credit--可限制用户不能使用信用卡支付
        "openid" to user.openId
//                电子发票入口开放标识	receipt	否	String(8)	Y	Y，传入Y时，支付成功消息和支付详情页将出现开票入口。需要在微信支付商户平台或微信公众平台开通电子发票功能，传此字段才可生效
//            +场景信息	scene_info	否	String(256)
//    {"store_info" : {
//        "id": "SZTX001",
//        "name": "腾大餐厅",
//        "area_code": "440305",
//        "address": "科技园中一路腾讯大厦" }}
//
//    该字段常用于线下活动时的场景信息上报，支持上报实际门店信息，商户也可以按需求自己上报相关信息。该字段为JSON对象数据，对象格式为{"store_info":{"id": "门店ID","name": "名称","area_code": "编码","address": "地址" }} ，字段详细说明请点击行前的+展开
    )
    // 貌似还是直接用map 最利索，，日。。
    val signedMap = signMap(data, account)

//    xmlMapper
    val x = WechatPayResponseHandler.xmlMapper.writeValueAsString(signedMap)
        .replace("LinkedHashMap>", "xml>")

//    println(x)

    return newClient().use {
        val method = HttpPost("https://api.mch.weixin.qq.com/pay/unifiedorder")

        val entity = EntityBuilder.create()
            .setContentType(ContentType.APPLICATION_XML)
            .setText(x)
            .build()

        method.entity = entity

        val response =
            it.execute(
                method,
                WechatPayResponseHandler(environment.acceptsProfiles(WechatSpringConfig.techTestProfile))
            )
        val prepayId =
            if (environment.acceptsProfiles(WechatSpringConfig.payMockProfile)) UUID.randomUUID().toSimpleString()
            else response.getStringOrError("prepay_id")

        wechatPayOrderRepository.save(
            WechatPayOrder(
                codeUrl = response.getOptionalString("code_url"),
                id = requestId,
                toPayOrderIdentify = order.getOrderToPayOrderIdentify(),
                prepayId = prepayId,
                account = account,
                user = user,
                amount = orderAmount
            )
        )
    }
}

private fun signMap(data: Map<String, Any?>, account: WechatPayAccount): Map<String, Any?> {
    val map = data.toMutableMap()
    map["nonce_str"] = UUID.randomUUID().toSimpleString()

    val sign = signValue(map, account)
    map["sign"] = sign
    return map
}


private fun verifySignMap(account: WechatPayAccount, map: Map<String, Any?>) {
    val sign = signValue(map, account)
    if (map["sign"] != sign)
        throw IllegalArgumentException("错误签名")
}

private fun signValue(
    map: Map<String, Any?>,
    account: WechatPayAccount
): String {
    val toSign = map
        .filter { it.key != "sign" }
        // 过滤空值
        .filter { it.value != null && it.value.toString().trim().isNotEmpty() }
        .toSortedMap()
        .map { "${it.key}=${it.value}&" }
        .joinToString("") + "key=" + account.payApiKey

//    println(toSign)
    val afterSign = MessageDigest.getInstance("MD5").digest(toSign.toByteArray())
    return Hex.encodeHexString(afterSign).toUpperCase(Locale.ENGLISH)
}

