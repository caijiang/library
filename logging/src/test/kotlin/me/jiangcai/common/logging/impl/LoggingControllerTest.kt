package me.jiangcai.common.logging.impl

import me.jiangcai.common.logging.LoggingConfigTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.servlet.config.annotation.EnableWebMvc

/**
 * @author CJ
 */
@ContextConfiguration(classes = [LoggingControllerTest.OpenWeb::class])
class LoggingControllerTest : LoggingConfigTest() {

    private val name = LoggingConfigTest::class.java.getPackage().name

    @Autowired
    private lateinit var environment: Environment

    @Test
    fun index() {
        val uri = environment.getProperty("jiangcai.logging.uri")
        mockMvc.perform(
            get(uri)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))

        mockMvc.perform(
            post(uri)
                .param("name", name)
                .param("level", "info")
        )
            .andExpect(status().isFound)

        disableDebug()
//
//        mockMvc.perform(
//            get(uri)
//        )
//            .andDo(print())
//            .andExpect(status().isOk)
//            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))

        mockMvc.perform(
            delete(("$uri/$name/"))

        ).andExpect(status().isFound)

        mockMvc.perform(
            post(uri)
                .param("name", name)
                .param("level", "debug")
        )
            .andExpect(status().isFound)

        enableDebug()


    }

    @Configuration
    @EnableWebMvc
    @PropertySource("classpath:/logging_ui.properties")
    open class OpenWeb
}