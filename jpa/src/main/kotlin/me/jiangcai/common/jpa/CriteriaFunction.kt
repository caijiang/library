package me.jiangcai.common.jpa

import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.Temporal
import java.time.temporal.WeekFields
import java.util.*
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Predicate

/**
 * @author CJ
 */
@Suppress("UNCHECKED_CAST")
open class CriteriaFunction(
    private val builder: CriteriaBuilder,
    private val timezoneDiff: String = "00:00"
) {

    /**
     * @return 易用的字符串链接结果
     */
    fun contact(vararg args: Expression<String>): Expression<String> {
        if (args.isEmpty())
            return builder.nullLiteral(String::class.java)
        if (args.size == 1)
            return args[0]
        if (args.size == 2)
            return builder.concat(args[0], args[1])
        // 将 0,1 合并，再用新的返回值 跟之后的几个值进行迭代
        val others = args.copyOfRange(2, args.size)

        return contact(builder.concat(args[0], args[1]), *others)
    }

    private val databaseFriendLyDateFormatter = DateTimeFormatter.ofPattern("yyyy-M-d", Locale.CHINA)

    private fun timezoneFixLocalDateTime(
        input: Expression<LocalDateTime>
    ): Expression<LocalDateTime> {
//        if (timezoneDiff == null)
//            return input
        return builder.function("ADDTIME", LocalDateTime::class.java, input, builder.literal(timezoneDiff))
    }

    private fun timezoneFixLocalDate(
        input: Expression<LocalDate>
    ): Expression<LocalDate> {
//        if (timezoneDiff == null)
//            return input
        return builder.function("ADDTIME", LocalDate::class.java, input, builder.literal(timezoneDiff))
    }

    //    常见的日期截取
    /**
     * @return 年份的表达式
     */
    fun <T : Temporal> year(arg: Expression<T>): Expression<Int> {
        if (arg.javaType == LocalDateTime::class.java)
            return builder.function("year", Int::class.java, timezoneFixLocalDateTime(arg as Expression<LocalDateTime>))
        if (arg.javaType == LocalDate::class.java)
            return builder.function("year", Int::class.java, timezoneFixLocalDate(arg as Expression<LocalDate>))
        throw IllegalStateException("unsupported of temporal type: ${arg.javaType}")
    }

    /**
     * @return 月份的表达式
     * @see Month.getValue
     */
    fun <T : Temporal> month(arg: Expression<T>): Expression<Int> {
        if (arg.javaType == LocalDateTime::class.java)
            return builder.function(
                "month",
                Int::class.java,
                timezoneFixLocalDateTime(arg as Expression<LocalDateTime>)
            )
        if (arg.javaType == LocalDate::class.java)
            return builder.function("month", Int::class.java, timezoneFixLocalDate(arg as Expression<LocalDate>))
        throw IllegalStateException("unsupported of temporal type: ${arg.javaType}")
    }

    /**
     * @return 日的表达式
     * @see Month.getValue
     */
    fun <T : Temporal> dayOfMonth(arg: Expression<T>): Expression<Int> {
        if (arg.javaType == LocalDateTime::class.java)
            return builder.function("day", Int::class.java, timezoneFixLocalDateTime(arg as Expression<LocalDateTime>))
        if (arg.javaType == LocalDate::class.java)
            return builder.function("day", Int::class.java, timezoneFixLocalDate(arg as Expression<LocalDate>))
        throw IllegalStateException("unsupported of temporal type: ${arg.javaType}")
    }

    /**
     * @param weekFields 周规格
     * @return 年第几周的表达式
     * @see WeekFields.weekOfWeekBasedYear
     */
    fun <T : Temporal> weekOfYear(arg: Expression<T>, weekFields: WeekFields = WeekFields.ISO): Expression<Int> {
        val mode = when (weekFields.firstDayOfWeek) {
            DayOfWeek.MONDAY -> when {
                weekFields.minimalDaysInFirstWeek >= 4 -> 3
                else -> 7
            }
            DayOfWeek.SUNDAY -> 6
//            DayOfWeek.SUNDAY -> when {
//                weekFields.minimalDaysInFirstWeek >= 4 -> 4
//                else -> 0
//            }
            else -> throw IllegalStateException("周只能从周一或者周日开始")
        }
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


    //<editor-fold desc="准确的日期比较">
    /**
     * 注意localDate是本地时间
     * @param localDate 如果某参数为传入值则推荐使用字符串，可以避免因为数据库的异常而导致类型异常；通常各个数据库对于字符串都是比较友好的
     * @return Predicate for same date
     */
    private fun <T : Temporal> dateEqual(arg: Expression<T>, localDate: String): Predicate {
        // arg 是来自数据库的，而date 是用户输入的，需要加入时区转换
        if (arg.javaType == LocalDateTime::class.java) {
            return dateEqual(timezoneFixLocalDateTime(arg as Expression<LocalDateTime>), builder.literal(localDate))
        }
        if (arg.javaType == LocalDate::class.java) {
            return dateEqual(timezoneFixLocalDate(arg as Expression<LocalDate>), builder.literal(localDate))
        }
        return dateEqual(arg, builder.literal(localDate))
    }

    /**
     * @return [dateEqual]
     */
    fun <T : Temporal> dateEqual(arg: Expression<T>, date: LocalDate): Predicate {
        return dateEqual(arg, date.format(databaseFriendLyDateFormatter))
    }

    /**
     * 是指同一天
     *
     * @return Predicate for same date
     */
    fun <X : Temporal> dateEqual(arg: Expression<X>, arg2: Expression<*>): Predicate {
        return builder.and(
            builder.equal(
                builder.function("year", Int::class.java, arg)
                , builder.function("year", Int::class.java, arg2)
            )
            , builder.equal(
                builder.function("month", Int::class.java, arg)
                , builder.function("month", Int::class.java, arg2)
            )
            , builder.equal(
                builder.function("day", Int::class.java, arg)
                , builder.function("day", Int::class.java, arg2)
            )
        )
    }
    //</editor-fold>


    //<editor-fold desc="日期比较">
    /**
     * 日期大于指定日期
     * 比如筛选日期
     * 2018.10.1 10:11:22  2018.9.30 ok
     * 2018.10.1 10:11:22  2018.10.1 acceptThisDate
     *
     * @param acceptThisDate 是否接受指定日期？
     */
    fun <T : Temporal> dateGreaterThan(
        arg: Expression<T>,
        date: LocalDate,
        acceptThisDate: Boolean = false
    ): Predicate {
        // 换算到 LocalDateTime 如果是 acceptThisDate 则是 >= thisDate_00:00:00
        if (acceptThisDate)
            return dateGE(arg, date.atStartOfDay())
        // 换算到 LocalDateTime 如果不是 acceptThisDate 则是 > (thisDate+1)_00:00:00
        return dateGT(arg, date.plusDays(1).atStartOfDay())
    }

    /**
     * 日期小于指定日期
     * @param acceptThisDate 是否接受指定日期
     */
    fun <T : Temporal> dateLessThan(
        arg: Expression<T>,
        date: LocalDate,
        acceptThisDate: Boolean = false
    ): Predicate {
        // 换算到 LocalDateTime 如果是 acceptThisDate 则是 < (thisDate+1)_00:00:00
        if (acceptThisDate)
            return dateLT(arg, date.plusDays(1).atStartOfDay())
        // 换算到 LocalDateTime 如果不是 acceptThisDate 则是 < thisDate_00:00:00
        return dateLT(arg, date.atStartOfDay())
    }

    fun <T : Temporal> dateLE(arg: Expression<T>, dateTime: LocalDateTime): Predicate {
        if (arg.javaType == LocalDateTime::class.java) {
            return builder.lessThanOrEqualTo(
                timezoneFixLocalDateTime(arg as Expression<LocalDateTime>)
                , builder.literal(databaseFriendFullDateFormatter.format(dateTime)).`as`(LocalDateTime::class.java)
            )
        }
        if (arg.javaType == LocalDate::class.java) {
            return builder.lessThanOrEqualTo(
                timezoneFixLocalDate(arg as Expression<LocalDate>)
                , builder.literal(databaseFriendFullDateFormatter.format(dateTime)).`as`(LocalDate::class.java)
            )
        }
        throw IllegalStateException("unsupported for temporal type:${arg.javaType}")
    }

    fun <T : Temporal> dateLT(arg: Expression<T>, dateTime: LocalDateTime): Predicate {
        if (arg.javaType == LocalDateTime::class.java) {
            return builder.lessThan(
                timezoneFixLocalDateTime(arg as Expression<LocalDateTime>)
                , builder.literal(databaseFriendFullDateFormatter.format(dateTime)).`as`(LocalDateTime::class.java)
            )
        }
        if (arg.javaType == LocalDate::class.java) {
            return builder.lessThan(
                timezoneFixLocalDate(arg as Expression<LocalDate>)
                , builder.literal(databaseFriendFullDateFormatter.format(dateTime)).`as`(LocalDate::class.java)
            )
        }
        throw IllegalStateException("unsupported for temporal type:${arg.javaType}")
    }

    fun <T : Temporal> dateGT(arg: Expression<T>, dateTime: LocalDateTime): Predicate {
        if (arg.javaType == LocalDateTime::class.java) {
            return builder.greaterThan(
                timezoneFixLocalDateTime(arg as Expression<LocalDateTime>)
                , builder.literal(databaseFriendFullDateFormatter.format(dateTime)).`as`(LocalDateTime::class.java)
            )
        }
        if (arg.javaType == LocalDate::class.java) {
            return builder.greaterThan(
                timezoneFixLocalDate(arg as Expression<LocalDate>)
                , builder.literal(databaseFriendFullDateFormatter.format(dateTime)).`as`(LocalDate::class.java)
            )
        }
        throw IllegalStateException("unsupported for temporal type:${arg.javaType}")
    }

    fun <T : Temporal> dateGE(arg: Expression<T>, dateTime: LocalDateTime): Predicate {
        if (arg.javaType == LocalDateTime::class.java) {
            return builder.greaterThanOrEqualTo(
                timezoneFixLocalDateTime(arg as Expression<LocalDateTime>)
                , builder.literal(databaseFriendFullDateFormatter.format(dateTime)).`as`(LocalDateTime::class.java)
            )
        }
        if (arg.javaType == LocalDate::class.java) {
            return builder.greaterThanOrEqualTo(
                timezoneFixLocalDate(arg as Expression<LocalDate>)
                , builder.literal(databaseFriendFullDateFormatter.format(dateTime)).`as`(LocalDate::class.java)
            )
        }
        throw IllegalStateException("unsupported for temporal type:${arg.javaType}")
    }
    //</editor-fold>


    //<editor-fold desc="年份和月比较">
    /**
     * @return 同年同月的谓语
     */
    fun <X : Temporal> yearAndMonthEqual(arg: Expression<X>, date: Expression<*>): Predicate {
        return builder.and(
            builder.equal(
                builder.function("year", Int::class.java, arg)
                , builder.function("year", Int::class.java, date)
            )
            , builder.equal(
                builder.function("month", Int::class.java, arg)
                , builder.function("month", Int::class.java, date)
            )
        )
    }

    /**
     * @param expression 数据库中的一个日期表达式
     * @return 同年同月的谓语
     */
    fun <T : Temporal> yearAndMonthEqual(expression: Expression<T>, localDate: LocalDate): Predicate {
        if (expression.javaType == LocalDateTime::class.java) {
            return yearAndMonthEqual(
                timezoneFixLocalDateTime(expression as Expression<LocalDateTime>),
                builder.literal(localDate.format(databaseFriendLyDateFormatter))
            )
        }
        if (expression.javaType == LocalDate::class.java) {
            return yearAndMonthEqual(
                timezoneFixLocalDate(expression as Expression<LocalDate>),
                builder.literal(localDate.format(databaseFriendLyDateFormatter))
            )
        }
        return yearAndMonthEqual(expression, builder.literal(localDate.format(databaseFriendLyDateFormatter)))
    }

    /**
     * @param expression 数据库中的一个日期表达式
     * @return 同年同月的谓语
     */
    fun <T : Temporal> yearAndMonthEqual(expression: Expression<T>, year: Int, month: Month): Predicate {
        return yearAndMonthEqual(expression, year, month.value)
    }

    /**
     * @param expression 数据库中的一个日期表达式
     * @return 同年同月的谓语
     */
    fun <T : Temporal> yearAndMonthEqual(expression: Expression<T>, year: Int, month: Int): Predicate {
        return yearAndMonthEqual(expression, LocalDate.of(year, month, 1))
    }
    //</editor-fold>

    private val databaseFriendFullDateFormatter = DateTimeFormatter.ofPattern("yyyy-M-d HH:mm:ss.SSS", Locale.CHINA)
    //<editor-fold desc="时间段的比较">
    //    时间段的比较，一般是指from,to,范围

    private fun durationInSeconds(from: Expression<LocalDateTime>, to: Expression<*>): Expression<Int> {
        return builder.diff(
            builder.function("to_seconds", Int::class.java, to),
            builder.function("to_seconds", Int::class.java, from)
        )
    }

    /**
     * @param from 开始时刻
     * @param to 截止时刻
     * @param duration 要求的时间段
     * @return 时间段大于要求的谓语
     */
    fun durationGT(from: Expression<LocalDateTime>, to: Expression<*>, duration: Duration): Predicate {
        val exp = durationInSeconds(from, to)
        return builder.gt(exp, duration.seconds)
    }

    /**
     * 大于等于版本的[durationGT]
     */
    fun durationGE(from: Expression<LocalDateTime>, to: Expression<*>, duration: Duration): Predicate {
        val exp = durationInSeconds(from, to)
        return builder.ge(exp, duration.seconds)
    }

    /**
     * 小于版本的[durationGT]
     */
    fun durationLT(from: Expression<LocalDateTime>, to: Expression<*>, duration: Duration): Predicate {
        val exp = durationInSeconds(from, to)
        return builder.lt(exp, duration.seconds)
    }

    /**
     * 小于等于版本的[durationGT]
     */
    fun durationLE(from: Expression<LocalDateTime>, to: Expression<*>, duration: Duration): Predicate {
        val exp = durationInSeconds(from, to)
        return builder.le(exp, duration.seconds)
    }

    /**
     * @param from 开始时刻
     * @param to 截止时刻
     * @param duration 要求的时间段
     * @return 时间段大于要求的谓语
     */
    fun durationGT(from: Expression<LocalDateTime>, to: LocalDateTime, duration: Duration): Predicate {
        return durationGT(
            timezoneFixLocalDateTime(from),
            builder.literal(to.format(databaseFriendFullDateFormatter)),
            duration
        )
    }

    /**
     * 大于等于版本的[durationGT]
     */
    fun durationGE(from: Expression<LocalDateTime>, to: LocalDateTime, duration: Duration): Predicate {
        return durationGE(
            timezoneFixLocalDateTime(from),
            builder.literal(to.format(databaseFriendFullDateFormatter)),
            duration
        )
    }

    /**
     * 小于版本的[durationGT]
     */
    fun durationLT(from: Expression<LocalDateTime>, to: LocalDateTime, duration: Duration): Predicate {
        return durationLT(
            timezoneFixLocalDateTime(from),
            builder.literal(to.format(databaseFriendFullDateFormatter)),
            duration
        )
    }

    /**
     * 小于等于版本的[durationGT]
     */
    fun durationLE(from: Expression<LocalDateTime>, to: LocalDateTime, duration: Duration): Predicate {
        return durationLE(
            timezoneFixLocalDateTime(from),
            builder.literal(to.format(databaseFriendFullDateFormatter)),
            duration
        )
    }
    //</editor-fold>

}