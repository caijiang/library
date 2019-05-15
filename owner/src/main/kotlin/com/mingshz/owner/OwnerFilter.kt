package com.mingshz.owner

import org.springframework.web.context.support.WebApplicationContextUtils
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 需在获得 context 之后，其他filter之前,包括安全
 * @author CJ
 */
class OwnerFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val context = WebApplicationContextUtils.getRequiredWebApplicationContext(request.servletContext)

        val service = context.getBean(FindOwnerService::class.java)
        val owner = service.findOwner(request)

        OwnerContext.updateContext(request, owner)
        try {
            filterChain.doFilter(request, response)
        } finally {
            OwnerContext.cleanContext(request)
        }
    }

}