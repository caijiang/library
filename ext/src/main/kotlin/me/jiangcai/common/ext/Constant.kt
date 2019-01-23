package me.jiangcai.common.ext


import java.math.BigDecimal

/**
 * 常量定义
 */
@Suppress("unused")
object Constant {
    /**
     * UTF8 编码
     */
    const val UTF8_ENCODING = "UTF-8"

    /**
     * 非空时间类型, mysql 专利
     */
    const val DATE_COLUMN_DEFINITION = "timestamp  NOT NULL DEFAULT CURRENT_TIMESTAMP"
    /**
     * 可为空的时间类型
     */
    const val DATE_NULL_ABLE_COLUMN_DEFINITION = "datetime"
    /**
     * float 类型的 scale
     */
    const val FLOAT_COLUMN_SCALE = 2
    /**
     * float 类型的 precision
     */
    const val FLOAT_COLUMN_PRECISION = 12

//    private const val DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss"
//    private const val DATE_FORMAT = "yyyy-MM-dd"
//    val dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT)!!
//    val dateTimeFormatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT)!!
    /**
     * 银行家舍入法
     */
    const val ROUNDING_MODE = BigDecimal.ROUND_HALF_EVEN


}
