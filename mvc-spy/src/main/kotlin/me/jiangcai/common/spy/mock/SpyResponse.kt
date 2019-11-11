package me.jiangcai.common.spy.mock

import javax.servlet.ServletOutputStream
import javax.servlet.http.HttpServletResponse

/**
 * @author CJ
 */
class SpyResponse(private val core: HttpServletResponse) : HttpServletResponse by core {
    private val output = SpyOutputStream(core.outputStream)
    override fun getOutputStream(): ServletOutputStream = output
}