package me.jiangcai.common.jpa.mysql

import me.jiangcai.common.jpa.CriteriaFunction
import org.eclipse.persistence.jpa.JpaCriteriaBuilder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.Temporal
import java.time.temporal.WeekFields
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Expression

/**
 * @author CJ
 */
class H2CriteriaFunction(builder: CriteriaBuilder, timezoneDiff: String) :
    CriteriaFunction(builder, timezoneDiff) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : Temporal> weekOfYear(arg: Expression<T>, weekFields: WeekFields): Expression<Int> {
        if (arg.javaType == LocalDateTime::class.java) {
            if (weekFields == WeekFields.ISO) {
                return builder.function(
                    "iso_week",
                    Int::class.java,
                    timezoneFixLocalDateTime(arg as Expression<LocalDateTime>)
                )
            }
            return builder.function(
                "WEEK",
                Int::class.java,
                timezoneFixLocalDateTime(arg as Expression<LocalDateTime>)
            )
        }

        if (arg.javaType == LocalDate::class.java) {
            if (weekFields == WeekFields.ISO) {
                return builder.function("iso_week", Int::class.java, timezoneFixLocalDate(arg as Expression<LocalDate>))
            }
            return builder.function(
                "WEEK",
                Int::class.java,
                timezoneFixLocalDate(arg as Expression<LocalDate>)
            )
        }
        throw IllegalStateException("unsupported of temporal type: ${arg.javaType}")
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Temporal> yearWeek(arg: Expression<T>, weekFields: WeekFields): Expression<Int> {
        val year = if (arg.javaType == LocalDateTime::class.java) {
            if (weekFields == WeekFields.ISO) {
                builder.function(
                    "iso_year",
                    Int::class.java,
                    timezoneFixLocalDateTime(arg as Expression<LocalDateTime>)
                )
            } else
                builder.function("year", Int::class.java, timezoneFixLocalDateTime(arg as Expression<LocalDateTime>))
        } else if (arg.javaType == LocalDate::class.java) {
            if (weekFields == WeekFields.ISO) {
                builder.function("iso_year", Int::class.java, timezoneFixLocalDate(arg as Expression<LocalDate>))
            } else
                builder.function("year", Int::class.java, timezoneFixLocalDate(arg as Expression<LocalDate>))
        } else
            throw IllegalStateException("unsupported of temporal type: ${arg.javaType}")

        return builder.sum(builder.prod(year, 100), weekOfYear(arg, weekFields))
    }

    private inline fun <reified X> ta(input: Expression<X>): Expression<X> {
        try {
            val list = timezoneDiff.split(":").map { it.toInt() }
            if (list[0] == 0 && list[1] == 0)
                return input
            if (input is org.eclipse.persistence.expressions.Expression) {
                @Suppress("UNCHECKED_CAST")
                return input.addDate("HOUR", list[0]).addDate("MINUTE", list[1])
                        as Expression<X>
            }
            val f1 = builder.function("date_add_hour", X::class.java, builder.literal(list[0]), input)
            return builder.function("date_add_minute", X::class.java, builder.literal(list[1]), f1)
        } catch (e: Exception) {
            return input
        }
    }

    override fun timezoneFixLocalDateTime(input: Expression<LocalDateTime>): Expression<LocalDateTime> {
        return ta(input)
    }

    override fun timezoneFixLocalDate(input: Expression<LocalDate>): Expression<LocalDate> {
        return ta(input)
    }

    override fun durationInSeconds(from: Expression<LocalDateTime>, to: Expression<*>): Expression<Int> {
        // 算是有2种类用法。一个是 eclipselink 的, 一种是 hibernate  的
        if (builder is JpaCriteriaBuilder) {
            val f1 = builder.toExpression(from)
            val f2 = builder.toExpression(to)
            val f3 = f1.dateDifference("second", f2)
            return builder.fromExpression(f3, Int::class.java)
        }
        // 如果不是，则采用
        return builder.function("date_diff_seconds", Int::class.java, from, to)
    }
}