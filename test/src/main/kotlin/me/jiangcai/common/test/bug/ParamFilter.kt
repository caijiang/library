package me.jiangcai.common.test.bug

import java.io.IOException
import java.util.*
import javax.servlet.*
import javax.servlet.http.HttpServletRequest

/**
 * @author CJ
 */
class ParamFilter : Filter {

    @Throws(ServletException::class)
    override fun init(filterConfig: FilterConfig) {

    }

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val m = HashMap(request.parameterMap)

        chain.doFilter(ParameterRequestWrapper(request as HttpServletRequest, m), response)
    }

    override fun destroy() {

    }
}