package me.jiangcai.common.jpa

import java.math.BigDecimal
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

    // 三角函数 放在一起吧

    //<editor-fold desc="Numeric and math">
    // abs 提供了
    /**
     * @return Return the smallest integer value not less than the argument
     */
    fun <T : Number> ceil(input: Expression<T>): Expression<Int> {
        return builder.function("ceil", Int::class.java, input)
    }

    /**
     * @return Return the largest integer value not greater than the argument
     */
    fun <T : Number> floor(input: Expression<T>): Expression<Int> {
        return builder.function("floor", Int::class.java, input)
    }

    /**
     * @return Raise to the power of
     */
    fun <T : Number> exp(input: Expression<T>): Expression<BigDecimal> {
        return builder.function("EXP", BigDecimal::class.java, input)
    }

//    /**
//     *
//     * If X is less than or equal to 0, or if B is less than or equal to 1, then NULL is returned.
//     * @return  the logarithm of X to the base B
//     */
//    fun <T:Number> log(b:Expression<T>,x:Expression<T>):Expression<BigDecimal> {
//
//    }

    // pow
    // Return the argument raised to the specified power

    //</editor-fold>

    //<editor-fold desc="常见的日期截取">
    /**
     * @param arg 本身保存在数据库内的数据的表达式，绝对不支持应用提交的参数
     * @return 24小时制的小时的表达式
     */
    fun <T : Temporal> hour(arg: Expression<T>): Expression<Int> {
        if (arg.javaType == LocalDateTime::class.java)
            return builder.function("hour", Int::class.java, timezoneFixLocalDateTime(arg as Expression<LocalDateTime>))
        if (arg.javaType == LocalDate::class.java)
            return builder.function("hour", Int::class.java, timezoneFixLocalDate(arg as Expression<LocalDate>))
        throw IllegalStateException("unsupported of temporal type: ${arg.javaType}")
    }

    /**
     * @param arg 本身保存在数据库内的数据的表达式，绝对不支持应用提交的参数
     * @return 分钟的表达式
     */
    fun <T : Temporal> minute(arg: Expression<T>): Expression<Int> {
        if (arg.javaType == LocalDateTime::class.java)
            return builder.function(
                "MINUTE",
                Int::class.java,
                timezoneFixLocalDateTime(arg as Expression<LocalDateTime>)
            )
        if (arg.javaType == LocalDate::class.java)
            return builder.function("MINUTE", Int::class.java, timezoneFixLocalDate(arg as Expression<LocalDate>))
        throw IllegalStateException("unsupported of temporal type: ${arg.javaType}")
    }

    /**
     * @param arg 本身保存在数据库内的数据的表达式，绝对不支持应用提交的参数
     * @return 秒的表达式
     */
    fun <T : Temporal> second(arg: Expression<T>): Expression<Int> {
        if (arg.javaType == LocalDateTime::class.java)
            return builder.function(
                "SECOND",
                Int::class.java,
                timezoneFixLocalDateTime(arg as Expression<LocalDateTime>)
            )
        if (arg.javaType == LocalDate::class.java)
            return builder.function("SECOND", Int::class.java, timezoneFixLocalDate(arg as Expression<LocalDate>))
        throw IllegalStateException("unsupported of temporal type: ${arg.javaType}")
    }

    /**
     * @param arg 本身保存在数据库内的数据的表达式，绝对不支持应用提交的参数
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
     * @param arg 本身保存在数据库内的数据的表达式，绝对不支持应用提交的参数
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
     * * 1,2,3 为季度1
     * * 4,5,6 为季度2
     * * 7,8,9 为季度3
     * * 10,11,12 为季度4
     * @return 季度的表达式
     */
    fun <T : Temporal> quarter(arg: Expression<T>): Expression<Int> {
        val m = month(arg)
        return ceil(builder.quot(m, 3))
    }

    /**
     * @param arg 本身保存在数据库内的数据的表达式，绝对不支持应用提交的参数
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
     * @param arg 本身保存在数据库内的数据的表达式，绝对不支持应用提交的参数
     * @param weekFields 周规格
     * @return 年第几周的表达式
     * @see WeekFields.weekOfWeekBasedYear
     */
    fun <T : Temporal> weekOfYear(arg: Expression<T>, weekFields: WeekFields = WeekFields.ISO): Expression<Int> {
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
            DayOfWeek.SUNDAY -> 6
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
    fun <T : Temporal> yearWeek(arg: Expression<T>, weekFields: WeekFields = WeekFields.ISO): Expression<Int> {
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
    //</editor-fold>


    //<editor-fold desc="准确的日期比较">
    /**
     * 注意localDate是本地时间
     * @param arg 本身保存在数据库内的数据的表达式，绝对不支持应用提交的参数
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

    /**
     * @param arg 本身保存在数据库内的数据的表达式，绝对不支持应用提交的参数
     */
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

    /**
     * @param arg 本身保存在数据库内的数据的表达式，绝对不支持应用提交的参数
     */
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

    /**
     * @param arg 本身保存在数据库内的数据的表达式，绝对不支持应用提交的参数
     */
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

    /**
     * @param arg 本身保存在数据库内的数据的表达式，绝对不支持应用提交的参数
     */
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
     * @param expression 本身保存在数据库内的数据的表达式，绝对不支持应用提交的参数
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

    private val databaseFriendFullDateFormatter = DateTimeFormatter.ofPattern("yyyy-M-d HH:mm:ss", Locale.CHINA)
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
     * @param from 开始时刻，本身保存在数据库内的数据的表达式，绝对不支持应用提交的参数
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
     * @param from 本身保存在数据库内的数据的表达式，绝对不支持应用提交的参数
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
     * @param from 本身保存在数据库内的数据的表达式，绝对不支持应用提交的参数
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
     * @param from 本身保存在数据库内的数据的表达式，绝对不支持应用提交的参数
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