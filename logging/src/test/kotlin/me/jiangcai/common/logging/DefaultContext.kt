package me.jiangcai.common.logging

import org.junit.Test
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.test.context.ContextConfiguration
import java.io.IOException

/**
 * @author CJ
 */
@ContextConfiguration(classes = [DefaultContext.DefaultContextConfig::class])
class DefaultContext : LoggingConfigTest() {

    @Configuration
    @PropertySource("classpath:/disableDebug.properties")
    open class DefaultContextConfig


    @Test
    @Throws(IOException::class)
    fun testNormal() {
        disableDebug()
    }
}