package me.jiangcai.common.spy

import com.jayway.jsonpath.JsonPath
import me.jiangcai.common.test.MvcTest
import org.junit.Test
import org.springframework.http.MediaType
import org.springframework.security.crypto.codec.Hex
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.web.filter.DelegatingFilterProxy
import java.security.MessageDigest
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
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)

        // POST /targets to create new one
        mockMvc.perform(
            post("$uri/targets")
                .contentType(MediaType.TEXT_PLAIN)
                .content("/echo")
//                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().is2xxSuccessful)

        mockMvc.perform(
            get("$uri/targets")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0]").value("/echo"))

        // now the application should spy all visits via /echo
        mockMvc.perform(
            get("/echo")
                .param("q", "1")
                .accept(MediaType.APPLICATION_JSON)
        )

        // find result
        mockMvc.perform(
            get("$uri/results")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").isString)
            .andExpect(jsonPath("$[0].url").isString)
            .andExpect(jsonPath("$[0].parameters").isArray)
            .andExpect(jsonPath("$[0].parameters[0].name").value("q"))
            .andExpect(jsonPath("$[0].parameters[0].value").value("1"))
            .andExpect(jsonPath("$[0].responseHeaders").isArray)
            .andExpect(jsonPath("$[0].requestHeaders").isArray)
            .andExpect(jsonPath("$[0].requestHeaders[0].name").value("Accept"))
            .andExpect(jsonPath("$[0].requestHeaders[0].value").value("application/json"))
            .andExpect(jsonPath("$[0].responseText").value("hello"))
            .andExpect(jsonPath("$[0].start").isNumber)
            .andExpect(jsonPath("$[0].end").isNumber)
            .andExpect(jsonPath("$[0].code").value(200))
            .andExpect(jsonPath("$[0].method").value("GET"))
            .andExpect(jsonPath("$[0].uri").value("/echo"))

        // remove the data.
        mockMvc.perform(
            delete("$uri/results")
        )
            .andExpect(status().is2xxSuccessful)
        // try for request with body.

        mockMvc.perform(
            post("/echo")
                .content("you")
        )

        val resultId = JsonPath.read<String>(
            mockMvc.perform(
                get("$uri/results")
                    .accept(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$[0].requestText").value("you"))
                .andExpect(jsonPath("$[0].code").value(202))
                .andExpect(jsonPath("$[0].method").value("POST"))
                .andExpect(jsonPath("$[0].uri").value("/echo"))
                .andReturn()
                .response
                .contentAsString, "$[0].id"
        )

        // also we can delete one by one.
        mockMvc.perform(
            delete("$uri/results/$resultId")
        )
            .andExpect(status().is2xxSuccessful)

        mockMvc.perform(
            get("$uri/results")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(0))

        // and target.

        // delete by md5
        val x = String(Hex.encode(MessageDigest.getInstance("MD5").digest("/echo".toByteArray())))
        mockMvc.perform(
            delete("$uri/targets/$x")
        )
            .andExpect(status().is2xxSuccessful)

        mockMvc.perform(
            get("$uri/targets")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(0))


        // then we need crack the error
        mockMvc.perform(
            post("$uri/targets")
                .contentType(MediaType.TEXT_PLAIN)
                .content("/error")
        )
            .andExpect(status().is2xxSuccessful)

        try {
            mockMvc.perform(
                get("/error")
            )
        } catch (e: Throwable) {
        }
        mockMvc.perform(
            get("$uri/results")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))


    }

}