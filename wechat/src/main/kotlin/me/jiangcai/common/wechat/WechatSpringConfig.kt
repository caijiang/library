package me.jiangcai.common.wechat

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Profiles
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * boot 中还需要通过 EntityScan 加入需要的entity包
 * 将提供 [WechatMockDataService],[WechatApiService],[WechatPayApiService]
 * @author CJ
 */
@Configuration
@EnableJpaRepositories("me.jiangcai.common.wechat.repository")
@ComponentScan("me.jiangcai.common.wechat.controller", "me.jiangcai.common.wechat.service")
class WechatSpringConfig(
//    @Suppress("SpringJavaInjectionPointsAutowiringInspection") @Autowired
//    private val requestMappingHandlerAdapter: RequestMappingHandlerAdapter
) : WebMvcConfigurer {

    companion object {
        /**
         * wechat_tech_test
         * 技术测试 profile
         * 在此模式下会跳过某些校验以及虚拟一些结果
         */
        val techTestProfile: Profiles = Profiles.of("wechat_tech_test")

        /**
         * wechat_pay_mock
         * 支付模拟
         * * 伪造 统一支付订单
         * * 支持模拟支付 [WechatApiService.mockPayOrderSuccess]
         */
        val payMockProfile: Profiles = Profiles.of("wechat_pay_mock")
    }

    //    @PostConstruct
//    fun well() {
//        sortIt(requestMappingHandlerAdapter.messageConverters)
//    }
//
    override fun extendMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        super.extendMessageConverters(converters)
        // make MappingJackson2HttpMessageConverter before MappingJackson2XmlHttpMessageConverter
        sortIt(converters)
    }

    private fun sortIt(converters: MutableList<HttpMessageConverter<*>>) {
        val xml = converters.find { it is MappingJackson2XmlHttpMessageConverter }
        val json = converters.find { it is MappingJackson2HttpMessageConverter }
        if (json != null && xml != null) {
            converters.remove(json)
            converters.add(converters.indexOf(xml), json)
        }
//        println(converters)
    }
}