package me.jiangcai.common.jpa.mysql

import me.jiangcai.common.jpa.CriteriaFunction
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.Temporal
import java.time.temporal.WeekFields
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Expression

/**
 * @author CJ
 */
class MysqlCriteriaFunction(builder: CriteriaBuilder, timezoneDiff: String) :
    CriteriaFunction(builder, timezoneDiff) {

    /**
     * @param arg 本身保存在数据库内的数据的表达式，绝对不支持应用提交的参数
     * @param weekFields 周规格
     * @return 年第几周的表达式
     * @see WeekFields.weekOfWeekBasedYear
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : Temporal> weekOfYear(arg: Expression<T>, weekFields: WeekFields): Expression<Int> {
        val mode = toWeekMode(weekFields)
        if (arg.javaType == LocalDateTime::class.java)
            return builder.function(
                "WEEK",
                Int::class.java,
                timezoneFixLocalDateTime(arg as Expression<LocalDateTime>),
                builder.literal(mode)
            )
        if (arg.javaType == LocalDate::class.java)
            return builder.function(
                "WEEK",
                Int::class.java,
                timezoneFixLocalDate(arg as Expression<LocalDate>),
                builder.literal(mode)
            )
        throw IllegalStateException("unsupported of temporal type: ${arg.javaType}")
    }

    private fun toWeekMode(weekFields: WeekFields): Int {
        return when (weekFields.firstDayOfWeek) {
            DayOfWeek.MONDAY -> when {
                weekFields.minimalDaysInFirstWeek >= 4 -> 3
                else -> 7
            }
            DayOfWeek.SUNDAY -> when {
                weekFields.minimalDaysInFirstWeek >= 4 -> 6
                else -> 4
            }
            //            DayOfWeek.SUNDAY -> when {
            //                weekFields.minimalDaysInFirstWeek >= 4 -> 4
            //                else -> 0
            //            }
            else -> throw IllegalStateException("周只能从周一或者周日开始")
        }
    }

    /**
     * @param arg 本身保存在数据库内的数据的表达式，绝对不支持应用提交的参数
     * @param weekFields 周规格
     * @return 年以及周的联合表达式; 等于 年*100+周
     * @see WeekFields.weekOfWeekBasedYear
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : Temporal> yearWeek(arg: Expression<T>, weekFields: WeekFields): Expression<Int> {
        val mode = toWeekMode(weekFields)
        if (arg.javaType == LocalDateTime::class.java)
            return builder.function(
                "YEARWEEK",
                Int::class.java,
                timezoneFixLocalDateTime(arg as Expression<LocalDateTime>),
                builder.literal(mode)
            )
        if (arg.javaType == LocalDate::class.java)
            return builder.function(
                "YEARWEEK",
                Int::class.java,
                timezoneFixLocalDate(arg as Expression<LocalDate>),
                builder.literal(mode)
            )
        throw IllegalStateException("unsupported of temporal type: ${arg.javaType}")
    }

    override fun durationInSeconds(from: Expression<LocalDateTime>, to: Expression<*>): Expression<Int> {
        return builder.diff(
            builder.function("to_seconds", Int::class.java, to),
            builder.function("to_seconds", Int::class.java, from)
        )
    }

    override fun timezoneFixLocalDateTime(input: Expression<LocalDateTime>): Expression<LocalDateTime> {
        return builder.function("ADDTIME", LocalDateTime::class.java, input, builder.literal(timezoneDiff))
    }

    override fun timezoneFixLocalDate(input: Expression<LocalDate>): Expression<LocalDate> {
        return builder.function("ADDTIME", LocalDate::class.java, input, builder.literal(timezoneDiff))
    }
}