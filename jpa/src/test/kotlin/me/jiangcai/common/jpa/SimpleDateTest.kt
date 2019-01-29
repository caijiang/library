package me.jiangcai.common.jpa

import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.temporal.WeekFields

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


        val t = LocalDate.of(2019, Month.JANUARY, 1)

        println(t.get(WeekFields.ISO.weekOfYear()))
        println(t.get(WeekFields.SUNDAY_START.weekOfYear()))

        println(t.get(WeekFields.ISO.weekOfWeekBasedYear()))
        println(t.get(WeekFields.SUNDAY_START.weekOfWeekBasedYear()))

        val t2 = LocalDate.of(2018, Month.DECEMBER, 31)

        println(t2.get(WeekFields.ISO.weekOfYear()))
        println(t2.get(WeekFields.SUNDAY_START.weekOfYear()))

        println(t2.get(WeekFields.ISO.weekOfWeekBasedYear()))
        println(t2.get(WeekFields.SUNDAY_START.weekOfWeekBasedYear()))
    }
}