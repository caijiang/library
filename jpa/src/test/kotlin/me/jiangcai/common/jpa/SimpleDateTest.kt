package me.jiangcai.common.jpa

import org.junit.Test
import java.time.LocalDateTime
import java.time.Month

/**
 * @author CJ
 */
class SimpleDateTest {
    @Test
    fun `对于日期规则的一些演示`() {
        val now = LocalDateTime.of(2019, Month.JANUARY, 1, 23, 59, 59)
        println(now)
        println(now.plusSeconds(1))
        println(now.plusSeconds(2))
    }
}