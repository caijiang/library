package me.jiangcai.common.logging

import org.junit.Test
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.test.context.ContextConfiguration
import java.io.IOException

/**
 * @author CJ
 */
@ContextConfiguration(classes = [ChangedContext.ChangedContextConfig::class])
class ChangedContext : LoggingConfigTest() {


    @Test
    @Throws(IOException::class)
    fun testNormal() {
        enableDebug()
    }

    @Configuration
    @PropertySource("classpath:/enableDebug.properties")
    open class ChangedContextConfig
}