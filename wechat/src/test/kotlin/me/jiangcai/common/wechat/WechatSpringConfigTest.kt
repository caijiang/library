package me.jiangcai.common.wechat

import me.jiangcai.common.ext.test.isEqualMoneyTo
import me.jiangcai.common.wechat.entity.WechatPayAccount
import me.jiangcai.common.wechat.entity.WechatPayOrder
import me.jiangcai.common.wechat.repository.WechatPayAccountRepository
import me.jiangcai.common.wechat.repository.WechatPayOrderRepository
import me.jiangcai.common.wechat.service.techMockDataInReqInRefundNotify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
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
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import java.io.File
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.imageio.ImageIO
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

/**
 * @author CJ
 */
@SpringBootTest
@ActiveProfiles("wechat_tech_test") // 技术测试
@AutoConfigureMockMvc
@Suppress("NonAsciiCharacters", "TestFunctionName", "UsePropertyAccessSyntax")
@ContextConfiguration(classes = [Config::class])
internal class WechatSpringConfigTest {

    @Autowired
    private var mockMvc: MockMvc? = null

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private lateinit var platformTransactionManager: PlatformTransactionManager

    @Autowired
    private lateinit var wechatPayApiService: WechatPayApiService

    @Autowired
    private lateinit var wechatApiService: WechatApiService

    @Autowired
    private lateinit var wechatMockDataService: WechatMockDataService

    @Autowired
    private lateinit var wechatPayAccountRepository: WechatPayAccountRepository

    @Autowired
    private lateinit var wechatPayOrderRepository: WechatPayOrderRepository

    @Test
    fun miniGetUnlimitedQRCode() {
        // 首先结果必须是一个图片
        val data = mockMvc!!.perform(
            get("/wechat/min/unlimitedQRCode")
                .param("scene", "well")
        )
            .andExpect(status().isOk)
            .andReturn().response.contentAsByteArray
        ImageIO.read(data.inputStream())

        File("./out/image.png")
            .writeBytes(data)
    }

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

        val account = findAccount()

        val wechatUser =
            wechatMockDataService.fetchWechatUser("o7R91wcYSGsqqK7UNfSqXQGMKUZs", appId = "wxcfb79dba92b5499d")
        val order = wechatPayApiService.createUnifiedOrderForMini(
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

                override fun getPayExpireTime(): LocalDateTime {
                    return LocalDateTime.now().plusMinutes(30)
                }

                override fun getOrderProductName(): String = "测试"

                override fun getOrderBody(): String = "内容"

            }
        )
        val cb = entityManager.criteriaBuilder
        val cq = cb.createQuery(Long::class.java)
        val root = cq.from(WechatPayOrder::class.java)

        assertThat(
            entityManager.createQuery(
                cq.select(cb.count(root))
                    .where(cb.equal(root, order), WechatPayOrder.toOrdinalSuccessPay(cb, root))
            )
                .singleResult
        )
            .`as`("刚下单 肯定没支付成功")
            .isEqualTo(0)

        println(order)
        assertThat(order.prepayId)
            .isNotNull()
        assertThat(order.ordinalSuccessPay)
            .isFalse()

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
        assertThat(wechatPayOrderRepository.getOne(order.id).orderSuccess)
            .isTrue()
        assertThat(wechatPayOrderRepository.getOne(order.id).ordinalSuccessPay)
            .isTrue()

        assertThat(
            entityManager.createQuery(
                cq.select(cb.count(root))
                    .where(cb.equal(root, order), WechatPayOrder.toOrdinalSuccessPay(cb, root))
            )
                .singleResult
        )
            .`as`("现在支付成功了")
            .isEqualTo(1)

        refundTest(order)
    }

    private fun findAccount(): WechatPayAccount {
        val x = File("./src/test/resources/mock.p12")
        val file = if (x.exists()) x else File("./wechat/src/test/resources/mock.p12")
        // 1442217602
        return wechatPayAccountRepository.findByIdOrNull("1498570872") ?: wechatPayAccountRepository.save(
            WechatPayAccount(
                p12FileName = file.absolutePath,
                merchantId = "1498570872",
                payApiKey = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456"
            )
        )
    }

    /**
     * 本地生成证书
     *
     * 退款测试。
     * 流程。
     *
     * 发起退款。JPA 查询。
     * 模拟退款回调，JPA 查询
     */
    private fun refundTest(order: WechatPayOrder) {
        val cb = entityManager.criteriaBuilder
        val cq = cb.createQuery(BigDecimal::class.java)
        val root = cq.from(WechatPayOrder::class.java)

        assertThat(
            entityManager.createQuery(
                cq.select(WechatPayOrder.toRefundAmount(cb, root))
                    .where(cb.equal(root, order))
            )
                .singleResult
        )
            .isEqualMoneyTo(BigDecimal.ZERO)

        // 退款 50%
        val refundMoney = "50".toBigDecimal()
        val refundOrder = wechatPayApiService.refundPayForMini(order, refundMoney)


        TransactionTemplate(platformTransactionManager)
            .executeWithoutResult {
                assertThat(
                    entityManager.createQuery(
                        cq.select(WechatPayOrder.toRefundAmount(cb, root))
                            .where(cb.equal(root, order))
                    )
                        .singleResult
                )
                    .isEqualMoneyTo(refundMoney)
                assertThat(wechatPayOrderRepository.getOne(order.id).refundAmount)
                    .isEqualMoneyTo(refundMoney)

                assertThat(
                    entityManager.createQuery(
                        cq.select(WechatPayOrder.toRefundSuccessAmount(cb, root))
                            .where(cb.equal(root, order))
                    )
                        .singleResult
                )
                    .isEqualMoneyTo(BigDecimal.ZERO)
                assertThat(wechatPayOrderRepository.getOne(order.id).refundSuccessAmount)
                    .isEqualMoneyTo(BigDecimal.ZERO)
            }


        // 现在模拟退款成功了

        val notifyJson = "<xml>\n" +
                "<return_code>SUCCESS</return_code>\n" +
                "   <appid><![CDATA[wxcfb79dba92b5499d]]></appid>\n" +
                "   <mch_id><![CDATA[${order.account.merchantId}]]></mch_id>\n" +
                "   <nonce_str><![CDATA[TeqClE3i0mvn3DrK]]></nonce_str>\n" +
                "   <req_info><![CDATA[T87GAHG17TGAHG1TGHAHAHA1Y1CIOA9UGJH1GAHV871HAGAGQYQQPOOJMXNBCXBVNMNMAJAA]]></req_info>\n" +
                "</xml>"
        techMockDataInReqInRefundNotify = "<root>\n" +
                "<out_refund_no><![CDATA[${refundOrder.id}]]></out_refund_no>\n" +
                "<out_trade_no><![CDATA[${refundOrder.id}]]></out_trade_no>\n" +
                "<refund_account><![CDATA[REFUND_SOURCE_RECHARGE_FUNDS]]></refund_account>\n" +
                "<refund_fee><![CDATA[${refundMoney.movePointRight(2)}]]></refund_fee>\n" +
                "<refund_id><![CDATA[${refundOrder.refundId}]]></refund_id>\n" +
                "<refund_recv_accout><![CDATA[支付用户零钱]]></refund_recv_accout>\n" +
                "<refund_request_source><![CDATA[API]]></refund_request_source>\n" +
                "<refund_status><![CDATA[SUCCESS]]></refund_status>\n" +
                "<settlement_refund_fee><![CDATA[${refundMoney.movePointRight(2)}]]></settlement_refund_fee>\n" +
                "<settlement_total_fee><![CDATA[${refundMoney.movePointRight(2)}]]></settlement_total_fee>\n" +
                "<success_time><![CDATA[2018-11-19 16:24:13]]></success_time>\n" +
                "<total_fee><![CDATA[${order.amount.movePointRight(2)}]]></total_fee>\n" +
                "<transaction_id><![CDATA[${order.payTransactionId}]]></transaction_id>\n" +
                "</root>"

        mockMvc!!.perform(
            post("/wechat/paymentNotify/refund")
                .contentType(MediaType.APPLICATION_XML)
                .content(notifyJson)
        )
            .andExpect(status().isOk)


        TransactionTemplate(platformTransactionManager)
            .executeWithoutResult {
                assertThat(
                    entityManager.createQuery(
                        cq.select(WechatPayOrder.toRefundAmount(cb, root))
                            .where(cb.equal(root, order))
                    )
                        .singleResult
                )
                    .isEqualMoneyTo(refundMoney)
                assertThat(wechatPayOrderRepository.getOne(order.id).refundAmount)
                    .isEqualMoneyTo(refundMoney)

                assertThat(
                    entityManager.createQuery(
                        cq.select(WechatPayOrder.toRefundSuccessAmount(cb, root))
                            .where(cb.equal(root, order))
                    )
                        .singleResult
                )
                    .isEqualMoneyTo(refundMoney)
                assertThat(wechatPayOrderRepository.getOne(order.id).refundSuccessAmount)
                    .isEqualMoneyTo(refundMoney)
            }

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