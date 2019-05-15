package com.mingshz.owner.support

import com.mingshz.owner.OwnerContext
import com.mingshz.owner.entity.OwnerEntity
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import javax.servlet.ServletRequest

/**
 * @author CJ
 */
class OwnerEntityResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == OwnerEntity::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter?,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any {
        return OwnerContext.getContext(webRequest.getNativeRequest(ServletRequest::class.java))
    }
}