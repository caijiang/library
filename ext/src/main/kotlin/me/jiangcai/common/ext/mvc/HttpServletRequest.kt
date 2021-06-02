package me.jiangcai.common.ext.mvc

import org.springframework.http.MediaType
import org.springframework.util.StringUtils
import javax.servlet.http.HttpServletRequest


/**
 * @return 当前访问上下文的URL 比如 http://localhost:8080 或者 https://foo.com 或者 https://foo.com/well
 */
fun HttpServletRequest.contextUrl(): String {
    val sb = StringBuilder()
    sb.append(scheme)
    sb.append("://")
    sb.append(serverName)
    if (serverPort > 0 && (!isSecure || serverPort != 433) && (isSecure || serverPort != 80)) {
        sb.append(":").append(serverPort)
    }
    sb.append(contextPath)
    return sb.toString()
}


/**
 * @return 访问者IP
 */
fun HttpServletRequest.clientIpAddress(): String {
    val xff = getHeader("X-Forwarded-For")
    return if (xff != null && xff.isNotEmpty()) {
        val ips = xff.trim { it <= ' ' }.split(",").toTypedArray()
        ips[0].trim()
    } else {
        remoteAddr
    }
}


/**
 * @return 是否乐意接受html响应
 * @author CJ
 */
@Suppress("unused")
fun HttpServletRequest.acceptHtml(): Boolean {
    val value = getHeader("Accept")

    // 没传 默认 不支持html
    if (!StringUtils.hasText(value))
        return false

    // 将 */* 排除掉，因为浏览器的行为肯定会带入text/html
    val types = MediaType.parseMediaTypes(value)
        .filter {
            it != MediaType.ALL
        }

    return types.any { it.isCompatibleWith(MediaType.TEXT_HTML) }
}