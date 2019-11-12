package me.jiangcai.common.spy

import me.jiangcai.common.spy.mock.SpyRequest
import me.jiangcai.common.spy.mock.SpyResponse
import me.jiangcai.common.spy.result.Record
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author CJ
 */
@Configuration("spyFilter")
open class SpyFilter : OncePerRequestFilter() {

    /**
     * target uris
     */
    val targets: MutableSet<Regex> = Collections.synchronizedSet(mutableSetOf<Regex>())

    val records: MutableList<Record> = Collections.synchronizedList(mutableListOf<Record>())

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (targets.any {
                request.requestURI?.matches(it) == true
            }) {
            val start = System.currentTimeMillis()
            // read the request, and
            val data = request.inputStream.readBytes()

            // mock the response, to record outputs.
            val res = SpyResponse(response)

            filterChain.doFilter(SpyRequest(request, data), res)
            // read all data.
            records.add(
                Record.toRecord(
                    start, request, data, response, res
                )
            )

            return
        } else
            filterChain.doFilter(request, response)
    }

}