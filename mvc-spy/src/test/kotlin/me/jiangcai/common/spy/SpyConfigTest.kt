package me.jiangcai.common.spy

import me.jiangcai.common.test.MvcTest
import org.junit.Test
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.web.filter.DelegatingFilterProxy
import javax.annotation.Resource
import javax.servlet.ServletContext

/**
 * 随机设置一个 uri
 * 并且开始 spy
 * 访问该 uri 获取html
 * @author CJ
 */
@ContextConfiguration(classes = [SpyTestConfig::class])
class SpyConfigTest : MvcTest() {

    @Resource
    private lateinit var servletContext: ServletContext

    override fun buildMockMVC(builder: DefaultMockMvcBuilder): DefaultMockMvcBuilder {
        val filter = DelegatingFilterProxy()
        filter.setTargetBeanName("spyFilter")
        filter.setServletContext(servletContext)
        return builder.addFilter(filter)
    }

    @Test
    fun spy() {
        val uri = "/102030"

        // visit the index
        mockMvc.perform(
            get(uri)
        ).andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))

        // GET /targets to view all spying
        mockMvc.perform(
            get("$uri/targets")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)

        // POST /targets to create new one
        mockMvc.perform(
            post("$uri/targets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\'/echo\'")
//                .accept(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().is2xxSuccessful)

        mockMvc.perform(
            get("$uri/targets")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0]").value("/echo"))

        // now the application should spy all visits via /echo
        mockMvc.perform(
            get("/echo")
        )

        // find result
        mockMvc.perform(
            get("$uri/results")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length").value(1))

        // TODO more test...

    }

}