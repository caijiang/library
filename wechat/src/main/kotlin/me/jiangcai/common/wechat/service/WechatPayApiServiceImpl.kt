package me.jiangcai.common.wechat.service

import com.fasterxml.jackson.databind.node.POJONode
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import me.jiangcai.common.ext.help.toSimpleString
import me.jiangcai.common.ext.mvc.clientIpAddress
import me.jiangcai.common.ext.mvc.contextUrl
import me.jiangcai.common.wechat.PayableOrder
import me.jiangcai.common.wechat.WechatPayApiService
import me.jiangcai.common.wechat.WechatSpringConfig
import me.jiangcai.common.wechat.entity.*
import me.jiangcai.common.wechat.event.WechatPaySuccessEvent
import me.jiangcai.common.wechat.event.WechatRefundSuccessEvent
import me.jiangcai.common.wechat.model.TradeType
import me.jiangcai.common.wechat.repository.WechatPayOrderRepository
import me.jiangcai.common.wechat.repository.WechatRefundOrderRepository
import me.jiangcai.common.wechat.util.WechatPayResponseHandler
import me.jiangcai.common.wechat.util.WechatResponse
import org.apache.commons.codec.binary.Hex
import org.apache.commons.logging.LogFactory
import org.apache.http.client.HttpResponseException
import org.apache.http.client.entity.EntityBuilder
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import java.math.BigDecimal
import java.security.MessageDigest
import java.security.Security
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.servlet.http.HttpServletRequest

/**
 * @author CJ
 */
@Service
class WechatPayApiServiceImpl(
    @Autowired
    internal val applicationContext: ApplicationContext,
    @Autowired
    internal val platformTransactionManager: PlatformTransactionManager,
    @Autowired
    internal val wechatPayOrderRepository: WechatPayOrderRepository,
    @Autowired
    internal val wechatRefundOrderRepository: WechatRefundOrderRepository,
    @Autowired
    val environment: Environment,
    @Value("\${me.jiangcai.wechat.payNotifyUri:/wechat/paymentNotify}")
    internal val payNotifyUri: String
) : WechatPayApiService {

    init {
        Security.addProvider(BouncyCastleProvider())
    }


    private fun String.fromWechatDecimal(): BigDecimal {
        return toBigDecimal().movePointLeft(2)
    }

    // 微信支付相关
    private val wechatDateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")

    private val wechatDateTimeFormatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    // 2017-12-15 09:46:01
    private fun String.toWechatDateTime2(): LocalDateTime {
        return LocalDateTime.from(
            wechatDateTimeFormatter2.parse(this)
        )
    }

    private fun String.toWechatDateTime(): LocalDateTime {
        return LocalDateTime.from(
            wechatDateTimeFormatter.parse(this)
        )
    }

    private val log = LogFactory.getLog(WechatPayApiServiceImpl::class.java)

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


    override fun createUnifiedOrderForMini(
        request: HttpServletRequest?,
        account: WechatPayAccount,
        user: WechatUser,
        order: PayableOrder,
        orderAmount: BigDecimal
    ): WechatPayOrder {

        val url = account.paymentNotifyUrlPrefix ?: request?.contextUrl()
        val requestId = UUID.randomUUID().toSimpleString()
        val data = mapOf(
            "appid" to user.appId,
            "mch_id" to account.merchantId,
//        设备号	device_info	否	String(32)	013467007045764	自定义参数，可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB"
            "body" to order.getOrderProductName(),
//        "detail" to order.getOrderBody(),
//        附加数据	attach	否	String(127)	深圳分店	附加数据，在查询API和支付通知中原样返回，可作为自定义参数使用。
            "out_trade_no" to requestId,
//        标价币种	fee_type	否	String(16)	CNY	符合ISO 4217标准的三位字母代码，默认人民币：CNY，详
            "total_fee" to orderAmount.movePointRight(2).intValueExact(),
            "spbill_create_ip" to request?.clientIpAddress(),
            "time_expire" to wechatDateTimeFormatter.format(order.getPayExpireTime()),
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

        log.debug("欲提交数据 $signedMap , xml: $x")

        return environment.newClient().use {
            val method = HttpPost("https://api.mch.weixin.qq.com/pay/unifiedorder")

            val entity = EntityBuilder.create()
                .setContentType(ContentType.APPLICATION_XML)
                .setBinary(x.toByteArray())
                .build()

            method.entity = entity

            val response =
                it.execute(
                    method,
                    WechatPayResponseHandler(
                        if (environment.acceptsProfiles(WechatSpringConfig.techTestProfile)) "<xml>\n" +
                                "   <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                                "   <return_msg><![CDATA[OK]]></return_msg>\n" +
                                "   <appid><![CDATA[wx2421b1c4370ec43b]]></appid>\n" +
                                "   <mch_id><![CDATA[10000100]]></mch_id>\n" +
                                "   <nonce_str><![CDATA[IITRi8Iabbblz1Jc]]></nonce_str>\n" +
                                "   <openid><![CDATA[oUpF8uMuAJO_M2pxb1Q9zNjWeS6o]]></openid>\n" +
                                "   <sign><![CDATA[7921E432F65EB8ED0CE9755F0E86D72F]]></sign>\n" +
                                "   <result_code><![CDATA[SUCCESS]]></result_code>\n" +
                                "   <prepay_id><![CDATA[wx201411101639507cbf6ffd8b0779950874]]></prepay_id>\n" +
                                "   <trade_type><![CDATA[JSAPI]]></trade_type>\n" +
                                "</xml>" else null
                    )
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

    override fun refundPayForMini(
        order: WechatPayOrder, amount: BigDecimal, reason: String?
        , account: RefundAccountType
    ): WechatRefundOrder {
        val refundOrder = WechatRefundOrder(
            payOrder = order,
            amount = amount,
            reason = reason,
            sourceType = RefundSourceType.API
        )

        return refundPayForMini(refundOrder, account)
    }

    override fun refundPayForMini(order: WechatRefundOrder, account: RefundAccountType): WechatRefundOrder {
        return refundPayForMiniImpl(order, account, true)
    }

    fun refundPayForMiniImpl(order: WechatRefundOrder, account: RefundAccountType, first: Boolean): WechatRefundOrder {
        val url = order.payOrder.account.paymentNotifyUrlPrefix

        if (order.requestSuccess)
            throw IllegalArgumentException("该退款请求已经发布")

        val data = mapOf(
            "appid" to order.payOrder.user.appId,
            "mch_id" to order.payOrder.account.merchantId,
            "transaction_id" to order.payOrder.payTransactionId,
            "out_refund_no" to order.id,
            "total_fee" to order.payOrder.amount.movePointRight(2).intValueExact(),
            "refund_fee" to order.amount.movePointRight(2).intValueExact(),
//        货币种类	refund_fee_type	否	String(8)	CNY
            "refund_desc" to order.reason,
            "refund_account" to account.name,
            "notify_url" to "$url${payNotifyUri}/refund"
        )

        val signedMap = signMap(data, order.payOrder.account)

//    xmlMapper
        val x = WechatPayResponseHandler.xmlMapper.writeValueAsString(signedMap)
            .replace("LinkedHashMap>", "xml>")

//    println(x)

        return environment.newClient(order.payOrder.account).use {
            val method = HttpPost("https://api.mch.weixin.qq.com/secapi/pay/refund")

            val entity = EntityBuilder.create()
                .setContentType(ContentType.APPLICATION_XML)
                .setBinary(x.toByteArray())
                .build()

            method.entity = entity

            val response = try {
                it.execute(
                    method,
                    WechatPayResponseHandler(
                        if (environment.acceptsProfiles(WechatSpringConfig.techTestProfile)) "<xml>\n" +
                                "   <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                                "   <return_msg><![CDATA[OK]]></return_msg>\n" +
                                "   <appid><![CDATA[wx2421b1c4370ec43b]]></appid>\n" +
                                "   <mch_id><![CDATA[10000100]]></mch_id>\n" +
                                "   <nonce_str><![CDATA[NfsMFbUFpdbEhPXP]]></nonce_str>\n" +
                                "   <sign><![CDATA[B7274EB9F8925EB93100DD2085FA56C0]]></sign>\n" +
                                "   <result_code><![CDATA[SUCCESS]]></result_code>\n" +
                                "   <transaction_id><![CDATA[1008450740201411110005820873]]></transaction_id>\n" +
                                "   <out_trade_no><![CDATA[1415757673]]></out_trade_no>\n" +
                                "   <out_refund_no><![CDATA[1415701182]]></out_refund_no>\n" +
                                "   <refund_id><![CDATA[2008450740201411110000174436]]></refund_id>\n" +
                                "   <refund_channel><![CDATA[]]></refund_channel>\n" +
                                "   <refund_fee>1</refund_fee>\n" +
                                "</xml>" else null
                    )
                )
            } catch (e: HttpResponseException) {
                if (environment.acceptsProfiles(WechatSpringConfig.payMockProfile))
                    WechatResponse(POJONode(Any()))
                else throw e
            }

//            {"return_code":"SUCCESS","return_msg":"OK","appid":"wxc2764391ade782f2","mch_id":"1593893201","nonce_str":"2WhnxdQSbPEeYNGE","sign":"1338380C79E6A494D4A395A4B763AF6E","result_code":"FAIL","err_code":"NOTENOUGH","err_code_des":"基本账户余额不足，请充值后重新发起"}
            order.refundId =
                if (environment.acceptsProfiles(WechatSpringConfig.payMockProfile)) UUID.randomUUID().toSimpleString()
                else response.getOptionalString("refund_id")

            // 订单已全额退款
            order.requestSuccess = if (environment.acceptsProfiles(WechatSpringConfig.payMockProfile)) true
            else response.getStringOrError("result_code") == "SUCCESS"
            order.requestErrorCode = if (environment.acceptsProfiles(WechatSpringConfig.payMockProfile)) null
            else response.getOptionalString("err_code")
            order.requestErrorMessage = if (environment.acceptsProfiles(WechatSpringConfig.payMockProfile)) null
            else response.getOptionalString("err_code_des")

            if (order.requestErrorCode == "ERROR" && order.requestErrorMessage == "订单已全额退款") {
                // 表示已经退款
                order.requestSuccess = true
                order.successWith(
                    settlementAmount = order.amount,
                    successTime = LocalDateTime.now(),
                    account = "主订单已全额退款",
                    accountType = account,
                    sourceType = RefundSourceType.API
                )
                return wechatRefundOrderRepository.save(order)
            }

            val refundOrder = wechatRefundOrderRepository.save(order)
            if (refundOrder.requestErrorCode == "NOTENOUGH" && first) {
                // 因为余额不足的导致的失败，那么重来。
                refundPayForMiniImpl(
                    refundOrder,
                    if (account == RefundAccountType.REFUND_SOURCE_RECHARGE_FUNDS) RefundAccountType.REFUND_SOURCE_UNSETTLED_FUNDS
                    else RefundAccountType.REFUND_SOURCE_RECHARGE_FUNDS, false
                )
            } else
                refundOrder
        }
    }

    override fun refundNotify(account: WechatPayAccount, appId: String, data: Map<String, Any?>) {
        if (data["return_code"] != "SUCCESS")
            return

        // create cipher
        // create cipher
//    val ecb = AES.ECB()
//
//    val aes = AESFastEngine()
//    val aesCBC = CBCBlockCipher(aes)
//    val aesCBCPadded = PaddedBufferedBlockCipher(aesCBC, PKCS7Padding())


        val hd5Key =
            Hex.encodeHexString(MessageDigest.getInstance("MD5").digest(account.payApiKey?.toByteArray())).toLowerCase()

        val secretKey = SecretKeySpec(hd5Key.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)

//    val result = cipher.doFinal(Base64.getDecoder().decode(data["req_info"].toString()))

        val info = if (environment.acceptsProfiles(WechatSpringConfig.techTestProfile)) XmlMapper().readTree(
            techMockDataInReqInRefundNotify
        ) else XmlMapper().readTree(cipher.doFinal(Base64.getDecoder().decode(data["req_info"].toString())))


        val status = info["refund_status"].textValue()
        val order = TransactionTemplate(platformTransactionManager).execute {
            val order = wechatRefundOrderRepository.getOne(info["out_refund_no"].textValue())
            if (order.refundId != info["refund_id"].textValue()) {
                log.warn("wechatPay notify with illegal refund_id: our side:${order.refundId} with notify side:${info["refund_id"]}")
                throw IllegalArgumentException("Illegal refund_id.")
            }

            val x = info["refund_fee"].textValue().fromWechatDecimal()
            if (order.amount != x) {
                log.warn("wechatPay notify with illegal refund_amount: our side:${order.amount} with notify side:${data["refund_fee"]}")
                throw IllegalStateException("Illegal refund_fee.")
            }

            order.requestSuccess = true

            when (status) {
                "SUCCESS" -> {
                    order.successWith(
                        info["settlement_refund_fee"].textValue().fromWechatDecimal()
                        ,
                        info["success_time"]?.textValue()?.toWechatDateTime2(), info["refund_recv_accout"].textValue()
                        ,
                        RefundAccountType.valueOf(info["refund_account"].textValue())
                        ,
                        RefundSourceType.valueOf(info["refund_request_source"].textValue())
                    )
                }
                "CLOSE" -> {
                    order.closeWith()
                }
                "CHANGE" -> {
                    order.changeWith()
                }
            }
            wechatRefundOrderRepository.save(order)
        }

        order?.let {
            if (it.success) {
                applicationContext.publishEvent(WechatRefundSuccessEvent(it, appId))
            }
        }


    }

    override fun payForMini(order: WechatPayOrder): Map<String, String> {
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

    override fun paymentNotify(account: WechatPayAccount, appId: String, data: Map<String, Any?>) {
        // https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=9_7&index=8
        if (data["return_code"] != "SUCCESS")
            return
        // 验签
        if (!environment.acceptsProfiles(WechatSpringConfig.techTestProfile))
            verifySignMap(account, data)

        // 成功支付的表示 result_code
        // transaction_id out_trade_no total_fee
        val order = TransactionTemplate(platformTransactionManager)
            .execute {
                val order = wechatPayOrderRepository.getOne(data["out_trade_no"].toString())
                if (order.amount.movePointRight(2)
                        .intValueExact() != BigDecimal(data["total_fee"].toString()).intValueExact()
                ) {
                    log.warn("wechatPay notify with illegal amount: our side:${order.amount} with notify side:${data["total_fee"]}")
                    throw IllegalStateException("Illegal total_fee")
                }
                if (data["result_code"] == "SUCCESS") {
                    order.payWith(data["transaction_id"].toString(), data["time_end"].toString().toWechatDateTime())
                } else if (data["result_code"] == "FAIL") {
                    order.failed()
                }

                wechatPayOrderRepository.save(order)
            }

        if (order!!.ordinalSuccessPay) {
            applicationContext.publishEvent(
                WechatPaySuccessEvent(
                    order, appId
                )
            )
        }
    }

    override fun mockPayOrderSuccess(wechatPayOrder: WechatPayOrder) {
        if (!environment.acceptsProfiles(WechatSpringConfig.payMockProfile)) {
            log.warn("无法在非模拟环境下使用模拟支付")
            return
        }
        val o = TransactionTemplate(platformTransactionManager)
            .execute {
                wechatPayOrder.payWith(
                    UUID.randomUUID().toSimpleString(), LocalDateTime.now()
                )
                wechatPayOrderRepository.save(wechatPayOrder)
            }

        applicationContext.publishEvent(
            WechatPaySuccessEvent(
                o!!, o.user.appId
            )
        )
    }

    override fun mockRefundOrderSuccess(order: WechatRefundOrder) {
        if (!environment.acceptsProfiles(WechatSpringConfig.payMockProfile)) {
            log.warn("无法在非模拟环境下使用模拟支付")
            return
        }
        val o = TransactionTemplate(platformTransactionManager)
            .execute {
                order.successWith(
                    order.amount, LocalDateTime.now(), "余额",
                    RefundAccountType.REFUND_SOURCE_RECHARGE_FUNDS, RefundSourceType.API
                )
                wechatRefundOrderRepository.save(order)
            }

        applicationContext.publishEvent(
            WechatRefundSuccessEvent(
                o!!, o.payOrder.user.appId
            )
        )
    }
}