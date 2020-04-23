package me.jiangcai.common.ss.impl

import me.jiangcai.common.ss.SystemStringService
import me.jiangcai.common.ss.SystemStringUpdatedEvent
import me.jiangcai.common.ss.entity.SystemString
import me.jiangcai.common.ss.repository.SystemStringRepository
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.util.NumberUtils
import java.math.BigDecimal
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * @author CJ
 */
@Service("systemStringService")
open class SystemStringServiceImpl(
    @Autowired
    private val applicationEventPublisher: ApplicationEventPublisher,
    @Autowired
    private val systemStringRepository: SystemStringRepository
) : SystemStringService {
    companion object {
        private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.CHINA)
        private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS", Locale.CHINA)
        private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.CHINA)
        private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss:SSS", Locale.CHINA)

        private val log = LogFactory.getLog(SystemStringServiceImpl::class.java)
    }


    override fun <T> getSystemString(key: String, exceptedType: Class<T>, defaultValue: T): T {
        val ss = systemStringRepository.findByIdOrNull(key) ?: return defaultValue
        return if (ss.value == null) defaultValue else toValue(exceptedType, ss.value)
    }

    override fun <T> getSystemString(key: String, exceptedType: Class<T>): T {
        val ss = systemStringRepository.findByIdOrNull(key) ?: throw IllegalStateException()
        return if (ss.value == null) throw IllegalStateException() else toValue(exceptedType, ss.value)
    }

    override fun <T> getCustomSystemString(
        key: String,
        comment: String?,
        runtime: Boolean,
        exceptedType: Class<T>,
        defaultValue: T
    ): T {
        val ss = systemStringRepository.findByIdOrNull(key) ?: systemStringRepository.save(SystemString(id = key))

        ss.comment = comment
        ss.custom = true
        ss.runtime = runtime
        ss.javaTypeName = exceptedType.name

        return if (ss.value == null) defaultValue else toValue(exceptedType, ss.value)

    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> toValue(type: Class<T>, value: String?): T {
        if (type == String::class.java)
            return value as T
        else if (type == BigDecimal::class.java)
            return BigDecimal(value) as T
        else if (type == LocalDateTime::class.java)
            return LocalDateTime.from(dateTimeFormatter.parse(value)) as T
        else if (type == LocalDate::class.java)
            return LocalDate.from(dateFormatter.parse(value)) as T
        else if (type == LocalTime::class.java)
            return LocalTime.from(timeFormatter.parse(value)) as T
        else if (type == Date::class.java)
            try {
                return simpleDateFormat.parse(value) as T
            } catch (e: ParseException) {
                log.info("pares $value into Date", e)
                throw IllegalStateException("pares $value into Date", e)
            }
        else if (type == Calendar::class.java) {
            try {
                val calendar = Calendar.getInstance()
                calendar.time = simpleDateFormat.parse(value)
                return calendar as T
            } catch (e: ParseException) {
                log.info("pares $value into Calendar", e)
                throw IllegalStateException("pares $value into Calendar", e)
            }

        } else if (type == Boolean::class.java || type == java.lang.Boolean.TYPE)
            return java.lang.Boolean.valueOf(value) as T
        else if (type == Char::class.java || type == Character.TYPE)
            return Character.valueOf(value!![0]) as T
        else if (type.isPrimitive) {
            return converter2Primitive(value, type)
        } else if (Number::class.java.isAssignableFrom(type)) {
            val numberType = type as Class<out Number>
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            return NumberUtils.parseNumber<Number>(value, numberType as Class<Number>?) as T
        } else
            throw IllegalArgumentException("not support type for:$type")
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> converter2Primitive(value: String?, primitiveType: Class<T>): T {
        if (primitiveType == java.lang.Byte.TYPE || primitiveType == Byte::class.java)
            return value!!.toByte() as T
        if (primitiveType == java.lang.Short.TYPE || primitiveType == Short::class.java)
            return value!!.toShort() as T
        if (primitiveType == Integer.TYPE || primitiveType == Integer::class.java)
            return value!!.toInt() as T
        if (primitiveType == java.lang.Long.TYPE || primitiveType == Long::class.java)
            return value!!.toLong() as T
        if (primitiveType == java.lang.Float.TYPE || primitiveType == Float::class.java)
            return value!!.toFloat() as T
        if (primitiveType == java.lang.Double.TYPE || primitiveType == Double::class.java)
            return value!!.toDouble() as T
        throw IllegalArgumentException("not support type for:$primitiveType")
    }

//    private fun toWrapperNumberClass(primitiveType: Class<*>): Class<out Number> {
//        if (primitiveType == java.lang.Byte.TYPE || primitiveType == Byte::class.java)
//            return Byte::class.java
//        if (primitiveType == java.lang.Short.TYPE)
//            return Short::class.java
//        if (primitiveType == Integer.TYPE)
//            return Int::class.java
//        if (primitiveType == java.lang.Long.TYPE)
//            return Long::class.java
//        if (primitiveType == java.lang.Float.TYPE)
//            return Float::class.java
//        if (primitiveType == java.lang.Double.TYPE)
//            return Double::class.java
//        throw IllegalArgumentException("not support type for:$primitiveType")
//    }

    override fun updateSystemString(key: String, decimal: BigDecimal?) {
        if (decimal == null) {
            u(key, null)
        } else {
            u(key, decimal.toString())
        }
        applicationEventPublisher.publishEvent(SystemStringUpdatedEvent(key, false, decimal))
    }

    override fun updateSystemString(key: String, value: String) {
        u(key, value)
        applicationEventPublisher.publishEvent(SystemStringUpdatedEvent(key, false, value))
    }

    private fun u(key: String, value: String?) {
        var ss = systemStringRepository.findByIdOrNull(key)
        if (ss == null) {
            ss = SystemString()
            ss.id = key
        }
        ss.value = value
        systemStringRepository.save(ss)
    }

    override fun updateSystemString(key: String, value: LocalDateTime?) {
        if (value == null) {
            u(key, null)
        } else {
            u(key, dateTimeFormatter.format(value))
        }
        applicationEventPublisher.publishEvent(SystemStringUpdatedEvent(key, false, value))
    }

    override fun updateSystemString(key: String, value: LocalDate?) {
        if (value == null) {
            u(key, null)
        } else {
            u(key, dateFormatter.format(value))
        }
        applicationEventPublisher.publishEvent(SystemStringUpdatedEvent(key, false, value))
    }

    override fun updateSystemString(key: String, value: LocalTime?) {
        if (value == null) {
            u(key, null)
        } else {
            u(key, timeFormatter.format(value))
        }
        applicationEventPublisher.publishEvent(SystemStringUpdatedEvent(key, false, value))
    }

    override fun updateSystemString(key: String, value: Date?) {
        if (value == null) {
            u(key, null)
        } else {
            u(key, simpleDateFormat.format(value))
        }
        applicationEventPublisher.publishEvent(SystemStringUpdatedEvent(key, false, value))
    }

    override fun updateSystemString(key: String, value: Calendar?) {
        if (value == null)
            u(key, null)
        else
            updateSystemString(key, value.time)
    }

    override fun delete(key: String) {
        if (systemStringRepository.findByIdOrNull(key) != null) {
            systemStringRepository.deleteById(key)
            applicationEventPublisher.publishEvent(SystemStringUpdatedEvent(key, true, null))
        }

    }

    override fun listCustom(): List<SystemString> {
        return systemStringRepository.findAll { root, _, cb ->
            cb.and(
                cb.isTrue(root.get("custom")), cb.isNotNull(root.get<Any>("javaTypeName"))
            )
        }
    }

}
