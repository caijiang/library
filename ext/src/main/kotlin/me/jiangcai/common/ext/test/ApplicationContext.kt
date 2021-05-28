package me.jiangcai.common.ext.test

import org.springframework.boot.test.web.client.LocalHostUriTemplateHandler
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.servlet.server.AbstractServletWebServerFactory
import org.springframework.context.ApplicationContext

/**
 * @param username 可选用户名
 * @param password 可选密码
 * @return 新建一个基于rest测试的模板
 */
fun ApplicationContext.createRestTemplate(username: String? = null, password: String? = null): TestRestTemplate {
    val webServerFactory: AbstractServletWebServerFactory = getBean(AbstractServletWebServerFactory::class.java)
    val sslEnabled = webServerFactory.ssl != null && webServerFactory.ssl.isEnabled
    val restTemplateBuilder = getBean(RestTemplateBuilder::class.java)
    val template = TestRestTemplate(
        restTemplateBuilder, username, password,
        *if (sslEnabled) arrayOf(
            TestRestTemplate.HttpClientOption.SSL,
            TestRestTemplate.HttpClientOption.ENABLE_COOKIES
        ) else arrayOf(
            TestRestTemplate.HttpClientOption.ENABLE_COOKIES
        )
    )
    val handler = LocalHostUriTemplateHandler(
        environment,
        if (sslEnabled) "https" else "http"
    )
    template.setUriTemplateHandler(handler)

    return template
}