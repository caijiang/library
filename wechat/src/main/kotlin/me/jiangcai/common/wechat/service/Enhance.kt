package me.jiangcai.common.wechat.service

import me.jiangcai.common.wechat.WechatSpringConfig
import me.jiangcai.common.wechat.entity.WechatPayAccount
import org.apache.http.client.config.RequestConfig
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.ssl.SSLContexts
import org.springframework.core.env.Environment

internal fun Environment.newClient(payAccount: WechatPayAccount? = null): CloseableHttpClient {
    val builder = HttpClientBuilder.create()
        .setDefaultRequestConfig(
            RequestConfig.custom()
                .setSocketTimeout(15000)
                .setConnectionRequestTimeout(15000)
                .setConnectTimeout(15000)
                .build()
        )

    val b = payAccount?.let {
        if (acceptsProfiles(WechatSpringConfig.payMockProfile)) {
            builder
        } else {
            // Trust own CA and all self-signed certs
            val context = SSLContexts.custom()
                .loadKeyMaterial(it.toKeyStore(), it.merchantId.toCharArray())//这里也是写密码的
                .build()
            // Allow TLSv1 protocol only
            val factory = SSLConnectionSocketFactory(
                context,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier()
            )
            builder.setSSLSocketFactory(factory)
        }
    } ?: builder

    return b
        .build()
}

var techMockDataInReqInRefundNotify: String = "<root>\n" +
        "<out_refund_no><![CDATA[131811191610442717309]]></out_refund_no>\n" +
        "<out_trade_no><![CDATA[71106718111915575302817]]></out_trade_no>\n" +
        "<refund_account><![CDATA[REFUND_SOURCE_RECHARGE_FUNDS]]></refund_account>\n" +
        "<refund_fee><![CDATA[3960]]></refund_fee>\n" +
        "<refund_id><![CDATA[50000408942018111907145868882]]></refund_id>\n" +
        "<refund_recv_accout><![CDATA[支付用户零钱]]></refund_recv_accout>\n" +
        "<refund_request_source><![CDATA[API]]></refund_request_source>\n" +
        "<refund_status><![CDATA[SUCCESS]]></refund_status>\n" +
        "<settlement_refund_fee><![CDATA[3960]]></settlement_refund_fee>\n" +
        "<settlement_total_fee><![CDATA[3960]]></settlement_total_fee>\n" +
        "<success_time><![CDATA[2018-11-19 16:24:13]]></success_time>\n" +
        "<total_fee><![CDATA[3960]]></total_fee>\n" +
        "<transaction_id><![CDATA[4200000215201811190261405420]]></transaction_id>\n" +
        "</root>"