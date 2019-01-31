package me.jiangcai.common.jpa

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.WeekFields

/**
 * @return year*100+week
 */
fun LocalDateTime.yearWeek(weekFields: WeekFields = WeekFields.ISO): Int {
    return toLocalDate().yearWeek(weekFields)
}

/**
 * @return year*100+week
 */
fun LocalDate.yearWeek(weekFields: WeekFields = WeekFields.ISO): Int {
    return get(weekFields.weekBasedYear()) * 100 + get(weekFields.weekOfWeekBasedYear())
}
