package me.jiangcai.common.wechat

import me.jiangcai.common.test.MvcTest
import org.junit.Ignore
import org.junit.Test
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * @author CJ
 */
@Suppress("NonAsciiCharacters", "TestFunctionName")
@ContextConfiguration(classes = [Config::class])
class WechatSpringConfigTest : MvcTest() {

    @Test
    fun go() {
        mockMvc.perform(
            post("/webSignature")
                .param("url", "http://www.baidu.com")
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.appId").isString)
            .andExpect(jsonPath("$.timestamp").isString)
            .andExpect(jsonPath("$.nonceStr").isString)
            .andExpect(jsonPath("$.signature").isString)
    }

    @Test
    @Ignore
    fun 小程序授权() {
        // http://api.mingshz.com/project/23/interface/api/64
        mockMvc.perform(
            get("/loginAsWechatApp")
                .param("code", "061QMmWZ0fVG6U1zMhUZ0xO3WZ0QMmWw")
        )
            .andDo(print())
            .andExpect(status().isOk)
        // 根据code 授权
        // GET https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code

//        openid	string	用户唯一标识
//        session_key	string	会话密钥
//        unionid	string	用户在开放平台的唯一标识符，在满足 UnionID 下发条件的情况下会返回，详见 UnionID 机制说明。
//        errcode	number	错误码
//        errmsg	string	错误信息
    }

}