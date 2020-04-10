package me.jiangcai.common.wechat

import me.jiangcai.common.wechat.entity.WechatPayAccount
import me.jiangcai.common.wechat.repository.WechatPayAccountRepository
import me.jiangcai.common.wechat.repository.WechatPayOrderRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal

/**
 * @author CJ
 */
@SpringBootTest
@ActiveProfiles("wechat_tech_test") // 技术测试
@AutoConfigureMockMvc
@Suppress("NonAsciiCharacters", "TestFunctionName")
@ContextConfiguration(classes = [Config::class])
internal class WechatSpringConfigTest {

    @Autowired
    private var mockMvc: MockMvc? = null

    @Autowired
    private lateinit var wechatApiService: WechatApiService

    @Autowired
    private lateinit var wechatMockDataService: WechatMockDataService

    @Autowired
    private lateinit var wechatPayAccountRepository: WechatPayAccountRepository

    @Autowired
    private lateinit var wechatPayOrderRepository: WechatPayOrderRepository

    @Test
    fun go() {
        mockMvc!!.perform(
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
    fun 小程序支付() {
        // 然后执行支付
        val request = MockHttpServletRequest()

        // 模拟一个微信商户

        val account = wechatPayAccountRepository.save(
            WechatPayAccount(
                merchantId = "1442217602",
                payApiKey = "1442217602"
            )
        )

        val wechatUser =
            wechatMockDataService.fetchWechatUser("o7R91wcYSGsqqK7UNfSqXQGMKUZs", appId = "wxcfb79dba92b5499d")
        val order = wechatApiService.createUnifiedOrderForMini(
            request,
            account,
            wechatUser,
            object : PayableOrder {
                override fun getOrderToPayOrderIdentify(): String {
                    return "9999"
                }

                override fun getOrderDueAmount(): BigDecimal {
                    return BigDecimal.valueOf(100)
                }

                override fun getOrderProductName(): String = "测试"

                override fun getOrderBody(): String = "内容"

            }
        )

        println(order)
        @Suppress("UsePropertyAccessSyntax")
        assertThat(order.prepayId)
            .isNotNull()

        // 然后此时给它一个……

        val notifyJson = "<xml>\n" +
                "  <appid><![CDATA[${wechatUser.appId}]]></appid>\n" +
                "  <attach><![CDATA[支付测试]]></attach>\n" +
                "  <bank_type><![CDATA[CFT]]></bank_type>\n" +
                "  <fee_type><![CDATA[CNY]]></fee_type>\n" +
                "  <is_subscribe><![CDATA[Y]]></is_subscribe>\n" +
                "  <mch_id><![CDATA[${account.merchantId}]]></mch_id>\n" +
                "  <nonce_str><![CDATA[5d2b6c2a8db53831f7eda20af46e531c]]></nonce_str>\n" +
                "  <openid><![CDATA[${wechatUser.openId}]]></openid>\n" +
                "  <out_trade_no><![CDATA[${order.id}]]></out_trade_no>\n" +
                "  <result_code><![CDATA[SUCCESS]]></result_code>\n" +
                "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                "  <sign><![CDATA[B552ED6B279343CB493C5DD0D78AB241]]></sign>\n" +
                "  <time_end><![CDATA[20140903131540]]></time_end>\n" +
                "  <total_fee>10000</total_fee>\n" +
                "<coupon_fee><![CDATA[10]]></coupon_fee>\n" +
                "<coupon_count><![CDATA[1]]></coupon_count>\n" +
                "<coupon_type><![CDATA[CASH]]></coupon_type>\n" +
                "<coupon_id><![CDATA[10000]]></coupon_id>\n" +
                "<coupon_fee><![CDATA[100]]></coupon_fee>\n" +
                "  <trade_type><![CDATA[JSAPI]]></trade_type>\n" +
                "  <transaction_id><![CDATA[1004400740201409030005092168]]></transaction_id>\n" +
                "</xml>"

        mockMvc!!.perform(
            post("/wechat/paymentNotify")
                .contentType(MediaType.APPLICATION_XML)
                .content(notifyJson)
        )
            .andExpect(status().isOk)

        // 然后订单就已经成功了
        assertThat(wechatPayOrderRepository.getOne(order.id).orderStatus)
            .isEqualTo("SUCCESS")

    }

//    @BeforeEach

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "decryptDataTestUserService")
    fun 小程序获取个人敏感信息() {
        // 首先已经登录了……
        // 提交用户详情，并且获得 unionId
        mockMvc!!.perform(
            post("/wechatMiniDecryptDataForUserInfo")
                .param(
                    "encryptedData",
                    "u3jx0xTqw4r1vb/Qh7e878NZMmKLGTNQN5eTdRll1c7uYNPggn+LoRLbMXBSLtgjmuSoviOdEE+OrG1QI8x2MhGZ+7JVXTwqmcae/PjwBlTCoul16TeYHjjTEwZnMly68R6+tW8hnRT1uRYmMq2D6tQoLJIEVFaIB+qNnXfhMoaZ6DCxA14BmpJp/+vGIOdGOgIK9ShYThJsnBgWPKGsb+D1B0nERqAAggKY6OKLJC+ga02jnfWxVJ1ccLtT8tPfovRjN/qXjhLrNDKzqJacQjCb3oa9NvkABpWP/IideW2/ALiBLNWDrgMuLNqHvKi2bw96cMIoLks32V9REl5YMxj/Wk0DoUP1enAY5kArj7tfObBBWUDVfbMxhRD5272QEG7pYXprN9qpaVKoJNO1AND3yQocWcxiogyEZVlu4LSjjKIawEXMBwKBOV+pkoL9wwCpRokUe/6MvqEOokLKDk694b0guBY4048sVVf2aRU="
                )
                .param("iv", "gqQU0rsYbhxo54CKuG9dUA==")
        )
//            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.nickname").isString)

    }

    //    @Test
    @Suppress("unused")
    fun 小程序授权() {
        // http://api.mingshz.com/project/23/interface/api/64
        mockMvc!!.perform(
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