package me.jiangcai.common.thymeleaf

import me.jiangcai.common.test.MvcTest
import org.junit.Test
import org.mockito.internal.matchers.Contains
import org.mockito.internal.matchers.Not
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * @author CJ
 */
@ContextConfiguration(classes = [ThymeleafViewConfigTestConfig::class])
class ThymeleafViewConfigTest : MvcTest() {

    @Test
    fun go() {
        mockMvc.perform(
            get("/hi")
        )
//            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(content().string(Not(Contains("你看不到我"))))
    }

}