package com.mingshz.login

import com.mingshz.login.test.TestConfig
import com.mingshz.login.test.entity.User
import me.jiangcai.common.test.MvcTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * 客户端
 * @author CJ
 */
@ContextConfiguration(classes = [TestConfig::class])
class ClassicLoginConfigTest : MvcTest() {

    @Autowired
    private lateinit var classicLoginService: ClassicLoginService<User>

    @Test
    fun go() {

        val u = User()
        u.username = randomMobile()
        val rawPassword = randomMobile()
        classicLoginService.newLogin(u, rawPassword)

        mockMvc.perform(
            post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsBytes(
                        mapOf(
                            "username" to u.username,
                            "password" to rawPassword
                        )
                    )
                )
        )
            .andExpect(status().isOk)
            .andExpect(content().string(u.username))


        mockMvc.perform(
            post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsBytes(
                        mapOf(
                            "username" to u.username,
                            "password" to rawPassword + rawPassword
                        )
                    )
                )
        )
            .andExpect(status().isUnauthorized)
    }

}