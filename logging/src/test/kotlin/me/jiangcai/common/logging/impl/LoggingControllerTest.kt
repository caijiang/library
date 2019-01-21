package me.jiangcai.common.logging.impl

import me.jiangcai.common.logging.LoggingConfigTest
import org.junit.Test
import org.springframework.context.annotation.Configuration
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

    @Test
    fun index() {
        mockMvc.perform(
            get("/loggingConfig")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))

        mockMvc.perform(
            post("/loggingConfig")
                .param("name", name)
                .param("level", "info")
        )
            .andExpect(status().isFound)

        disableDebug()

        mockMvc.perform(
            delete(("/loggingConfig/$name/"))

        ).andExpect(status().isFound)

        mockMvc.perform(
            post("/loggingConfig")
                .param("name", name)
                .param("level", "debug")
        )
            .andExpect(status().isFound)

        enableDebug()


    }

    @Configuration
    @EnableWebMvc
    open class OpenWeb
}