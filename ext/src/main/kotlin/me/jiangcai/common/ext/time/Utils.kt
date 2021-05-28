@file:Suppress("unused")

package me.jiangcai.common.ext.time

import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * 按照 [java.time.format.DateTimeFormatter.ISO_LOCAL_DATE] 格式解析 [LocalDate]
 */
fun String.toISOLocalDate(): LocalDate {
    val x = DateTimeFormatter.ISO_LOCAL_DATE.parse(this)
    return LocalDate.from(x)
}


fun LocalDate.toDate(): Date {
    val zonedDateTime: ZonedDateTime = atStartOfDay(ZoneId.systemDefault())
    return Date.from(zonedDateTime.toInstant())
}