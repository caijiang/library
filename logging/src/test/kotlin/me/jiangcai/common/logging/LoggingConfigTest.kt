package me.jiangcai.common.logging

import me.jiangcai.common.test.MvcTest
import org.apache.commons.logging.LogFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.runner.RunWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import java.io.FileInputStream
import java.io.IOException
import java.nio.charset.Charset

/**
 * @author CJ
 */
@ContextConfiguration(classes = [LoggingConfig::class])
@RunWith(SpringJUnit4ClassRunner::class)
abstract class LoggingConfigTest : MvcTest() {
    private val log = LogFactory.getLog(LoggingConfigTest::class.java)


    @Throws(IOException::class)
    fun enableDebug() {
        log.error("error")

        assertThat(lastLogMessage())
            .isEqualTo("error")
            .`as`("看不到错误信息")

        log.debug("debug")
        assertThat(lastLogMessage())
            .isEqualTo("debug")
            .`as`("应该记录debug信息")
    }

    @Throws(IOException::class)
    fun disableDebug() {
        log.error("error")

        assertThat(lastLogMessage())
            .isEqualTo("error")
            .`as`("看不到错误信息")

        log.debug("debug")
        assertThat(lastLogMessage())
            .isNotEqualTo("debug")
            .`as`("不应该记录debug信息")
    }


    @Throws(IOException::class)
    protected fun lastLogMessage(): String {
        val fileInputStream = FileInputStream("target/log.log")
        return fileInputStream.reader(Charset.forName("UTF-8"))
            .readLines()
            .last()
    }

}