package me.jiangcai.common.spy.mock

import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest

/**
 * @author CJ
 */
class SpyRequest(private val core: HttpServletRequest, data: ByteArray) : HttpServletRequest by core {
    private val input = SpyInputStream(data)
    override fun getInputStream(): ServletInputStream = input
}