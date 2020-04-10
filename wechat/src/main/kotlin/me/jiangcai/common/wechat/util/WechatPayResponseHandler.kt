package me.jiangcai.common.wechat.util

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import org.apache.http.HttpEntity
import org.apache.http.impl.client.AbstractResponseHandler

/**
 * @author CJ
 */
class WechatPayResponseHandler(
    private val testValue: Boolean = false
) : AbstractResponseHandler<WechatResponse>() {

    companion object {
        val xmlMapper: XmlMapper = XmlMapper.xmlBuilder().build()
    }

    override fun handleEntity(entity: HttpEntity): WechatResponse {
        if (testValue) {
            val testXml = "<xml>\n" +
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
                    "</xml>"
            return WechatResponse(xmlMapper.readTree(testXml))
        }
        return WechatResponse(xmlMapper.readTree(entity.content))
    }
}