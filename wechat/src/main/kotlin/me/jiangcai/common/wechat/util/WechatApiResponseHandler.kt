package me.jiangcai.common.wechat.util

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.HttpEntity
import org.apache.http.impl.client.AbstractResponseHandler

/**
 * @author CJ
 */
class WechatApiResponseHandler : AbstractResponseHandler<WechatResponse>() {

    companion object {
        val objectMapper = ObjectMapper()
    }

    override fun handleEntity(entity: HttpEntity): WechatResponse {
        return WechatResponse(objectMapper.readTree(entity.content))
    }
}