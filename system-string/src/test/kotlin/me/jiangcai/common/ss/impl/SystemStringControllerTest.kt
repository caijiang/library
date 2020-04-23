package me.jiangcai.common.ss.impl

import me.jiangcai.common.ss.SystemStringConfig
import me.jiangcai.common.ss.SystemStringService
import me.jiangcai.common.ss.SystemStringServiceTest
import me.jiangcai.common.ss.entity.SystemString
import org.apache.commons.lang3.RandomStringUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.core.env.Environment
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import java.net.URLEncoder
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

/**
 * @author CJ
 */
@ContextConfiguration(classes = [SystemStringConfig::class, SystemStringControllerTest.Config::class, SystemStringServiceTest.Config::class])
@SpringBootTest
@AutoConfigureMockMvc
class SystemStringControllerTest {


    @Autowired
    private lateinit var environment: Environment

    @Autowired
    private lateinit var systemStringService: SystemStringService

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Autowired
    private lateinit var mockMvc: MockMvc


    @Test
    @Throws(Exception::class)
    //    @Transactional
    fun go() {
        systemStringService.getCustomSystemString("test.key", null, true, String::class.java, "hello")
        val uri = environment.getProperty("jiangcai.ss.uri")!!

        mockMvc.perform(
            get(uri)
                .locale(Locale.CHINA)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)

        val content = RandomStringUtils.randomAlphabetic(10)
        mockMvc.perform(
            put(uri)
                .param("id", URLEncoder.encode("test.key", "UTF-8"))
                .contentType(MediaType.TEXT_PLAIN)
                .content(content)
        )
            .andExpect(status().is2xxSuccessful)

        assertThat(systemStringService.getCustomSystemString("test.key", null, true, String::class.java, "hello"))
            .isEqualTo(content)

        mockMvc.perform(delete(uri).param("id", URLEncoder.encode("test.key", "UTF-8")))
            .andExpect(status().is2xxSuccessful)

        assertThat(systemStringService.getCustomSystemString("test.key", null, true, String::class.java, "hello"))
            .isEqualTo("hello")

        val criteriaBuilder = entityManager.criteriaBuilder
        val cq = criteriaBuilder.createQuery(String::class.java)
        val root = cq.from(SystemString::class.java)
        println(
            entityManager.createQuery(
                cq
                    .select(root.get("value"))
                    .where(criteriaBuilder.equal(root.get<String>("id"), "test.key"))
            ).singleResult
        )

    }


    @Configuration
    @EnableWebMvc
    @PropertySource("classpath:/sys_ui.properties")
    internal open class Config {
        @Bean
        open fun messageSource(): MessageSource {
            val messageSource = ReloadableResourceBundleMessageSource()
            messageSource.setBasename("classpath:/testMessage")
            return messageSource
        }
    }
}