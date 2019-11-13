package me.jiangcai.common.spy.result

import me.jiangcai.common.spy.mock.SpyResponse
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import java.io.ByteArrayOutputStream
import java.io.PrintWriter
import java.nio.charset.Charset
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author CJ
 */
data class Record(
    val id: String = UUID.randomUUID().toString().replace("-", ""),
    val start: Long,
    val end: Long,
    val method: String,
    val uri: String,
    val url: String,
    val parameters: List<NV>,
    val requestHeaders: List<NV>,
    val responseHeaders: List<NV>,
    val code: Int,
    private val request: ByteArray,
    private val response: ByteArray,
    /**
     * too big to show
     */
    val responseText: String? = if (response.size > 1024 * 1024) null else response.toString(Charset.forName("UTF-8")),
    /**
     * too big to show
     */
    val requestText: String? = if (request.size > 1024 * 1024) null else request.toString(Charset.forName("UTF-8"))
) {
    companion object {
        fun toRecord(
            start: Long,
            request: HttpServletRequest,
            data: ByteArray,
            response: HttpServletResponse? = null,
            res: SpyResponse? = null,
            ex: Throwable? = null
        ): Record {
            return Record(
                start = start, end = System.currentTimeMillis(), method = request.method, uri = request.requestURI
                , url = request.requestURL.toString(), parameters = request.parametersList()
                , requestHeaders = request.headersList()
                , responseHeaders = response?.headersList() ?: emptyList()
                , code = response?.status ?: 500
                , request = data
                , response = res?.output?.data?.toByteArray() ?: {
                    if (ex == null)
                        "????? SPY Error!"
                    else {
                        val buf = ByteArrayOutputStream()
                        PrintWriter(buf, true)
                            .use {
                                ex.printStackTrace(it)
                                it.flush()
                            }
                        buf.toByteArray().toString(Charset.forName("UTF-8"))
                            .replace("\n\t", "<br />")
                            .replace("\n", "<br />")
                    }
                }().toByteArray()
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Record) return false

        if (id != other.id) return false
        if (start != other.start) return false
        if (end != other.end) return false
        if (uri != other.uri) return false
        if (url != other.url) return false
        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + start.hashCode()
        result = 31 * result + end.hashCode()
        result = 31 * result + uri.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + code
        return result
    }

    fun createResponseForResponse(): ResponseEntity<ByteArray> {
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(
                response
            )
    }

    fun createResponseForRequest(): ResponseEntity<ByteArray> {
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(
                request
            )
    }
}

private fun HttpServletRequest.headersList(): List<NV> {
    val list = mutableListOf<NV>()
    val names = headerNames
    while (names.hasMoreElements()) {
        val name = names.nextElement()
        val headers = getHeaders(name)
        while (headers.hasMoreElements()) {
            list.add(NV(name, headers.nextElement()))
        }
    }
    return list
}

private fun HttpServletResponse.headersList(): List<NV> {
    val list = mutableListOf<NV>()
    headerNames.forEach { name ->
        getHeaders(name).forEach {
            list.add(NV(name, it))
        }
    }
    return list
}

private fun HttpServletRequest.parametersList(): List<NV> {
    val list = mutableListOf<NV>()
    val names = parameterNames
    while (names.hasMoreElements()) {
        val name = names.nextElement()
        getParameterValues(name).forEach {
            list.add(NV(name, it))
        }
    }
    return list
}
