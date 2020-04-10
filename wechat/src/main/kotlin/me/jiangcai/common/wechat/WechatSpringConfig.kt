package me.jiangcai.common.wechat

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * boot 中还需要通过 EntityScan 加入需要的entity包
 * 将提供 [WechatMockDataService]和[WechatApiService]
 * @author CJ
 */
@Configuration
@EnableJpaRepositories("me.jiangcai.common.wechat.repository")
@ComponentScan("me.jiangcai.common.wechat.controller", "me.jiangcai.common.wechat.service")
//@EnableWebMvc
class WechatSpringConfig : WebMvcConfigurer {


    override fun extendMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        super.extendMessageConverters(converters)
        // make MappingJackson2HttpMessageConverter before MappingJackson2XmlHttpMessageConverter
        val xml = converters.find { it is MappingJackson2XmlHttpMessageConverter }
        val json = converters.find { it is MappingJackson2HttpMessageConverter }
        if (json != null && xml != null) {
            converters.remove(json)
            converters.add(converters.indexOf(xml), json)
        }
        println(converters)
    }
}