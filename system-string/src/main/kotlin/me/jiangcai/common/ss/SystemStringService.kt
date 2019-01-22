package me.jiangcai.common.ss

import me.jiangcai.common.ss.entity.SystemString
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

/**
 * @author CJ
 */
interface SystemStringService {


    /**
     * 获取配置值
     *
     * @param key          key;跟[org.springframework.context.MessageSource.getMessage]的code是一致的
     * @param exceptedType 期待的类型
     * @param defaultValue 默认数据
     * @param <T>          范型，支持所有的基本数据类型以及各种时间类型
     * @return 默认数据或者当前值
    </T> */
    @Transactional(readOnly = true)
    fun <T> getSystemString(key: String, exceptedType: Class<T>, defaultValue: T): T

    /**
     * 获取配置值
     * @param key          key;跟[org.springframework.context.MessageSource.getMessage]的code是一致的
     * @param exceptedType 期待的类型
     * @throws IllegalStateException 如果不存在
     */
    @Transactional(readOnly = true)
    @Throws(IllegalStateException::class)
    fun <T> getSystemString(key: String, exceptedType: Class<T>): T

    /**
     * 获取支持用户可配置的配置值
     *
     * @param <T>          范型，支持所有的基本数据类型以及各种时间类型
     * @param key          key;跟[org.springframework.context.MessageSource.getMessage]的code是一致的
     * @param comment      说明;跟[org.springframework.context.MessageSource.getMessage]的code是一致的
     * @param runtime      告知系统，运行时改变该值是否可以马上发挥作用
     * @param exceptedType 期待的类型
     * @param defaultValue 默认数据   @return 默认数据或者当前值
    </T> */
    @Transactional
    fun <T> getCustomSystemString(
        key: String,
        comment: String? = null,
        runtime: Boolean,
        exceptedType: Class<T>,
        defaultValue: T
    ): T

    // 基本
    @Transactional
    fun updateSystemString(key: String, value: Boolean) {
        updateSystemString(key, value.toString())
    }

    @Transactional
    fun updateSystemString(key: String, value: Byte) {
        updateSystemString(key, value.toString())
    }

    @Transactional
    fun updateSystemString(key: String, value: Short) {
        updateSystemString(key, value.toString())
    }

    @Transactional
    fun updateSystemString(key: String, value: Char) {
        updateSystemString(key, value.toString())
    }

    @Transactional
    fun updateSystemString(key: String, value: Int) {
        updateSystemString(key, value.toString())
    }

    @Transactional
    fun updateSystemString(key: String, value: Double) {
        updateSystemString(key, value.toString())
    }

    @Transactional
    fun updateSystemString(key: String, value: Long) {
        updateSystemString(key, value.toString())
    }

    @Transactional
    fun updateSystemString(key: String, value: Float) {
        updateSystemString(key, value.toString())
    }

    @Transactional
    fun updateSystemString(key: String, decimal: BigDecimal?)

    @Transactional
    fun updateSystemString(key: String, value: String)

    @Transactional
    fun updateSystemString(key: String, value: LocalDateTime?)

    @Transactional
    fun updateSystemString(key: String, value: LocalDate?)

    @Transactional
    fun updateSystemString(key: String, value: LocalTime?)

    @Transactional
    fun updateSystemString(key: String, value: Date?)

    @Transactional
    fun updateSystemString(key: String, value: Calendar?)

    @Transactional
    fun delete(key: String)

    @Transactional(readOnly = true)
    fun listCustom(): List<SystemString>
}