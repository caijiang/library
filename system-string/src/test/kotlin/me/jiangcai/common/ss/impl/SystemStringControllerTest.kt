package me.jiangcai.common.ss.impl

import me.jiangcai.common.ss.SystemStringConfig
import me.jiangcai.common.ss.SystemStringService
import me.jiangcai.common.ss.SystemStringServiceTest
import me.jiangcai.common.ss.entity.SystemString
import me.jiangcai.common.test.MvcTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.core.env.Environment
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import java.net.URLEncoder
import java.util.*
import javax.persistence.EntityManager

/**
 * @author CJ
 */
@ContextConfiguration(classes = [SystemStringConfig::class, SystemStringControllerTest.Config::class, SystemStringServiceTest.Config::class])
class SystemStringControllerTest : MvcTest() {


    @Autowired
    private lateinit var environment: Environment
    @Autowired
    private lateinit var systemStringService: SystemStringService
    @Autowired
    private lateinit var entityManager: EntityManager


    @Test
    @Throws(Exception::class)
    //    @Transactional
    fun go() {
        systemStringService.getCustomSystemString("test.key", null, true, String::class.java, "hello")
        val uri = environment.getProperty("jiangcai.ss.uri")

        mockMvc.perform(
            get(uri)
                .locale(Locale.CHINA)
        )
            .andDo(print())
            .andExpect(status().isOk)

        val content = randomEmailAddress()
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