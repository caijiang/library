package com.mingshz.login.wechat

import com.mingshz.login.wechat.test.WechatTestConfig
import me.jiangcai.common.test.MvcTest
import org.junit.Test
import org.mockito.internal.matchers.EndsWith
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.net.URLEncoder

/**
 * @author CJ
 */
@ContextConfiguration(classes = [WechatTestConfig::class])
class WechatLoginConfigTest : MvcTest() {

    /**
     * Create a [MockHttpServletRequestBuilder] for a GET request.
     *
     * @param urlTemplate  a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     */
    private fun wechatGet(urlTemplate: String, vararg urlVariables: Any?): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.get(urlTemplate, *urlVariables)
            .header("user-agent", "MicroMessenger")
    }

    @Test
    fun go() {

        mockMvc.perform(wechatGet("/test"))
            .andExpect(status().isNotFound)

        // 检查当前的 wechat openId
        val url = randomHttpURL()

        // 因为这个时候 还没有获得微信用户，所以无需处理
        val nextUri = "/wechat/authCore?url=${URLEncoder.encode(url, "UTF-8")}"
        val session = mockMvc.perform(
            wechatGet("/wechat/auth")
                .param("url", url)
        )
            .andExpect(status().isFound)
            .andExpect(header().string("location", EndsWith(nextUri)))
            .andReturn()
            .request.session as MockHttpSession

        mockMvc
            .perform(
                wechatGet("/wechat/authCore")
                    .param("url", url)
                    .session(session)
            )
            .andExpect(status().isFound)
            .andExpect(header().string("location", url))

        // 现在直接访问也可以得到一样的效果
        mockMvc.perform(
            wechatGet("/wechat/auth")
                .param("url", url)
                .session(session)
        )
            .andExpect(status().isFound)
            .andExpect(header().string("location", url))

        // 同时可以获得详情
        mockMvc.perform(
            wechatGet("/wechat/detail")
                .session(session)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))


    }


}