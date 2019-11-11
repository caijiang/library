package me.jiangcai.common.spy.result

import me.jiangcai.common.spy.mock.SpyResponse
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author CJ
 */
data class Record(
    val start: LocalDateTime,
    val end: LocalDateTime,
    val method: String,
    val uri: String
) {
    companion object {
        fun toRecord(
            start: LocalDateTime,
            request: HttpServletRequest,
            data: ByteArray,
            response: HttpServletResponse,
            res: SpyResponse
        ): Record {
            return Record(
                start = start, end = LocalDateTime.now(), method = request.method, uri = request.requestURI
            )
        }
    }
}