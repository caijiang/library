package com.mingshz.login

import com.mingshz.login.test.TestConfig
import com.mingshz.login.test.entity.User
import me.jiangcai.common.test.classic.ClassicMvcTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.temporal.ChronoUnit

/**
 * 客户端
 * @author CJ
 */
@ContextConfiguration(classes = [TestConfig::class])
class ClassicLoginConfigTest : ClassicMvcTest() {

    @Autowired
    private lateinit var classicLoginService: ClassicLoginService<User>
    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Test
    fun go() {
        println(applicationContext)

        val u = User()
        u.username = randomMobile()
        val rawPassword = randomMobile()
        val user = classicLoginService.newLogin(u, rawPassword)

        // 用户密码登录
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

        // 快速登录
        val uriBuilder = StringBuilder("/mine")
        val session = mockMvc.perform(
            get(uriBuilder.toString())
        )
            .andExpect(status().isUnauthorized)
            .andReturn()
            .request
            .session

        classicLoginService.requestToken(user, ChronoUnit.DAYS, 1L, uriBuilder)


        mockMvc.perform(
            get(uriBuilder.toString())
                .session(session as MockHttpSession)
        )
            .andExpect(status().isOk)


        mockMvc.perform(
            get("/mine")
                .session(session)
        )
            .andExpect(status().isOk)

        mockMvc.perform(
            get("/mine", user)
        )
            .andExpect(status().isOk)

        // 没登录给 401 权限不足给  403
        mockMvc.perform(
            get("/advancedMine", user)
        )
            .andExpect(status().isForbidden)

    }

}