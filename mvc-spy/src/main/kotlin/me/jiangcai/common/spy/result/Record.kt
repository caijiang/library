package me.jiangcai.common.spy.result

import me.jiangcai.common.spy.mock.SpyResponse
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
    /**
     * too big to show
     */
    val responseText: String? = null,
    /**
     * too big to show
     */
    val requestText: String? = null
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
                , requestText = data.toString(Charset.forName("UTF-8"))
                , responseText = res?.output?.data?.toByteArray()?.toString(Charset.forName("UTF-8"))
                    ?: {
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
                    }()
            )
        }
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
