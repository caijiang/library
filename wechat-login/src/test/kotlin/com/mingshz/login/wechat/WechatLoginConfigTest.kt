package com.mingshz.login.wechat

import com.mingshz.login.ClassicLoginService
import com.mingshz.login.test.entity.User
import com.mingshz.login.wechat.test.WechatTestConfig
import me.jiangcai.common.test.MvcTest
import me.jiangcai.common.test.hot.asWechatRequest
import org.junit.Test
import org.mockito.internal.matchers.EndsWith
import org.springframework.beans.factory.annotation.Autowired
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
            .asWechatRequest()
    }

    @Autowired
    private lateinit var classicLoginService: ClassicLoginService<User>

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

        val nextUri2 = "/wechat/authLogin?url=${URLEncoder.encode(url, "UTF-8")}"
        mockMvc
            .perform(
                wechatGet("/wechat/authCore")
                    .param("url", url)
                    .session(session)
            )
            .andExpect(status().isFound)
            .andExpect(header().string("location", EndsWith(nextUri2)))

        // 继续
        mockMvc
            .perform(
                wechatGet("/wechat/authLogin")
                    .param("url", url)
                    .session(session)
            )
            .andDo(print())
            .andExpect(status().isFound)
            .andExpect(header().string("location", url))

        // 现在直接访问也可以得到一样的效果，因为这个微信还没有绑定过什么人，所以还会再次auth
        mockMvc.perform(
            wechatGet("/wechat/auth")
                .param("url", url)
                .session(session)
        )
            .andExpect(status().isFound)
            .andExpect(header().string("location", EndsWith(nextUri2)))

        // 同时可以获得详情
        mockMvc.perform(
            wechatGet("/wechat/detail")
                .session(session)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))

        // 好了，现在我们把当前这个微信 绑定到某一个帐号上。
        val u = User()
        u.username = randomMobile()
        val rawPassword = randomMobile()
        val user = classicLoginService.newLogin(u, rawPassword)

        mockMvc.perform(
            wechatGet("/bind/${user.id}")
                .session(session)
        )
            .andExpect(status().isAccepted)

        // 再次登录
        mockMvc.perform(
            wechatGet("/wechat/auth")
                .param("url", url)
                .session(session)
        )
            .andExpect(status().isFound)
            .andExpect(header().string("location", EndsWith(nextUri2)))
        mockMvc
            .perform(
                wechatGet("/wechat/authLogin")
                    .param("url", url)
                    .session(session)
            )
            .andExpect(status().isFound)
            .andExpect(header().string("location", url))

        // 现在无需再跳转了
        mockMvc.perform(
            wechatGet("/wechat/auth")
                .param("url", url)
                .session(session)
        )
            .andExpect(status().isFound)
            .andExpect(header().string("location", url))
    }


}