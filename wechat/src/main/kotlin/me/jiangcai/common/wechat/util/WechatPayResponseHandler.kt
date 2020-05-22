package me.jiangcai.common.wechat.util

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import org.apache.http.HttpEntity
import org.apache.http.impl.client.AbstractResponseHandler

/**
 * @author CJ
 */
class WechatPayResponseHandler(
    private val testResponseXml: String? = null
) : AbstractResponseHandler<WechatResponse>() {

    companion object {
        val xmlMapper: XmlMapper = XmlMapper.xmlBuilder().build()
    }

    override fun handleEntity(entity: HttpEntity): WechatResponse {
        if (testResponseXml != null) {
            return WechatResponse(xmlMapper.readTree(testResponseXml))
        }
        return WechatResponse(xmlMapper.readTree(entity.content))
    }
}