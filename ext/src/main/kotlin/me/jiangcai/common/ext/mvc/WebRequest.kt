@file:Suppress("unused")

package me.jiangcai.common.ext.mvc

import org.springframework.web.context.request.WebRequest
import java.math.BigDecimal
import java.math.BigInteger

fun WebRequest.getStringParameter(name: String, default: String = ""): String {
    return getParameter(name) ?: default
}

fun WebRequest.getStringParameterOrNull(name: String): String? {
    return getParameter(name)
}

fun WebRequest.getBooleanParameter(name: String, default: Boolean = false): Boolean {
    return getParameter(name)?.toBoolean() ?: default
}

fun WebRequest.getBooleanParameterOrNull(name: String): Boolean? {
    return getParameter(name)?.toBoolean()
}

fun WebRequest.getByteParameter(name: String, default: Byte): Byte {
    return getParameter(name)?.toByteOrNull() ?: default
}

fun WebRequest.getByteParameterOrNull(name: String): Byte? {
    return getParameter(name)?.toByteOrNull()
}

fun WebRequest.getShortParameter(name: String, default: Short): Short {
    return getParameter(name)?.toShortOrNull() ?: default
}

fun WebRequest.getShortParameterOrNull(name: String): Short? {
    return getParameter(name)?.toShortOrNull()
}

fun WebRequest.getIntParameter(name: String, default: Int): Int {
    return getParameter(name)?.toIntOrNull() ?: default
}

fun WebRequest.getIntParameterOrNull(name: String): Int? {
    return getParameter(name)?.toIntOrNull()
}

fun WebRequest.getLongParameter(name: String, default: Long): Long {
    return getParameter(name)?.toLongOrNull() ?: default
}

fun WebRequest.getLongParameterOrNull(name: String): Long? {
    return getParameter(name)?.toLongOrNull()
}

fun WebRequest.getFloatParameter(name: String, default: Float): Float {
    return getParameter(name)?.toFloatOrNull() ?: default
}

fun WebRequest.getFloatParameterOrNull(name: String): Float? {
    return getParameter(name)?.toFloatOrNull()
}

fun WebRequest.getDoubleParameter(name: String, default: Double): Double {
    return getParameter(name)?.toDoubleOrNull() ?: default
}

fun WebRequest.getDoubleParameterOrNull(name: String): Double? {
    return getParameter(name)?.toDoubleOrNull()
}

fun WebRequest.getBigIntegerParameter(name: String, default: BigInteger): BigInteger {
    return getParameter(name)?.toBigIntegerOrNull() ?: default
}

fun WebRequest.getBigIntegerParameterOrNull(name: String): BigInteger? {
    return getParameter(name)?.toBigIntegerOrNull()
}

fun WebRequest.getBigDecimalParameter(name: String, default: BigDecimal): BigDecimal {
    return getParameter(name)?.toBigDecimalOrNull() ?: default
}

fun WebRequest.getBigDecimalParameterOrNull(name: String): BigDecimal? {
    return getParameter(name)?.toBigDecimalOrNull()
}
