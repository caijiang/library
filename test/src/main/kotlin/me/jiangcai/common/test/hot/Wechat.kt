package me.jiangcai.common.test.hot

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder

/**
 * 让它变成在微信中发起的请求
 */
fun MockHttpServletRequestBuilder.asWechatRequest(): MockHttpServletRequestBuilder {
    return this.header("user-agent", "MicroMessenger")
}
