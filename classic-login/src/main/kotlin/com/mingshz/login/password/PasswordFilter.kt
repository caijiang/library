@file:Suppress("UNCHECKED_CAST")

package com.mingshz.login.password

import com.mingshz.login.ClassicLoginConfig
import com.mingshz.login.LoginDelegate
import com.mingshz.login.entity.Login
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.DependsOn
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.HttpMediaTypeNotSupportedException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author CJ
 */
@DependsOn("authenticationManager")
class PasswordFilter(
    @Autowired
    private val loginDelegate: LoginDelegate
) : ApplicationContextAware, AbstractAuthenticationProcessingFilter(
    AntPathRequestMatcher(
        ClassicLoginConfig.loginUri,
        ClassicLoginConfig.loginMethod
    )
) {
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        authenticationManager = applicationContext.getBean(AuthenticationManager::class.java)
    }

    init {
        setAuthenticationSuccessHandler { request, response, authentication ->
            loginDelegate.authenticationSuccess(request, response, authentication, authentication.principal as Login)
        }
        setAuthenticationFailureHandler { request, response, exception ->
            loginDelegate.authenticationFailure(request, response, exception)
        }
    }

    lateinit var converters: List<HttpMessageConverter<*>>

    companion object {
        private val logger = LogFactory.getLog(PasswordFilter::class.java)

        private fun <T> readHttpMessage(
            type: Class<T>,
            request: HttpServletRequest,
            messageConverters: List<HttpMessageConverter<*>>
        ): T {
            val inputMessage = ServletServerHttpRequest(request)
            val contentType = inputMessage.headers.contentType

            val allSupportedMediaTypes = ArrayList<MediaType>()
            for (messageConverter in messageConverters) {
                messageConverter as HttpMessageConverter<Any>
                allSupportedMediaTypes.addAll(messageConverter.supportedMediaTypes)
                if (messageConverter.canRead(type, contentType)) {
                    if (logger.isDebugEnabled) {
                        logger.debug(
                            "Reading [" + type + "] as \"" + contentType
                                    + "\" using [" + messageConverter + "]"
                        )
                    }
                    return messageConverter.read(type, inputMessage) as T
                }
            }
            throw HttpMediaTypeNotSupportedException(contentType, allSupportedMediaTypes)
        }
    }

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse?): Authentication {
        if (request.method != ClassicLoginConfig.loginMethod) {
            throw AuthenticationServiceException(
                "Authentication method not supported: " + request.method
            )
        }
        val data = readHttpMessage(Map::class.java, request, converters)
        val token = UsernamePasswordAuthenticationToken(
            data[ClassicLoginConfig.loginRequestUsernameParameterName] ?: ""
            , data[ClassicLoginConfig.loginRequestPasswordParameterName] ?: ""
        )

        return this.authenticationManager.authenticate(token)
    }

}

private val HttpServletRequest.mediaType: MediaType
    get() {
        return MediaType.valueOf(this.contentType)
    }
