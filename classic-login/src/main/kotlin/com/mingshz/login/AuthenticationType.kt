package com.mingshz.login

import org.springframework.security.authentication.AuthenticationProvider
import javax.servlet.Filter

/**
 * 认证类型
 * @author CJ
 */
interface AuthenticationType {
    /**
     * 负责认证的提供者
     */
    fun authenticationProvider(): AuthenticationProvider

    /**
     * 拦截认证的过滤器
     */
    fun authenticationFilter(): Filter
}