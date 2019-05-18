package com.mingshz.login

import com.mingshz.login.entity.Login
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import java.lang.annotation.Inherited
import kotlin.reflect.KClass

/**
 * 启用经典身份系统，提供以下功能:
 *
 * 1. 提供bean [ClassicLoginService]
 * 1. 基于特定URI的用户名密码登录系统
 * 1. 基于特定token登录的快速登录系统,参考[ClassicLoginService.requestToken]
 * 1. 提供方法级别的安全保护
 * 1. 提供密码修改PUT /password
 * @author CJ
 */
@Suppress("unused")
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention
@MustBeDocumented
@Inherited
//@EnableWebMvc
@Import(ClassicLoginConfigPrefix::class, ClassicLoginConfig::class, ClassicLoginSecurityConfig::class)
annotation class EnableClassicLogin(
    /**
     * 额外载入的配置类
     */
    val loginExtraConfigClasses: Array<KClass<*>> = [],
    /**
     * 额外可载入[AuthenticationType]的配置类名
     */
    val loginExtraAuthenticationTypeConfigClasses: Array<String> = [],
    /**
     * 强制通过token授权的token参数名称
     * 通过请求中携带有这个请求参数都可进行强制登录，并且继续业务处理
     */
    val forceAuthenticationTokenParameterName: String = "_token",
    /**
     * 启用经典登录的实体类全限定名称
     */
    val loginClass: KClass<out Login>,
    /**
     * 提供登录的uri
     */
    val loginUri: String = "/login",
    /**
     * 登录方法
     */
    val loginMethod: String = "POST",
    /**
     * 允许的登录的请求数据类型
     */
    val loginRequestContentType: String = MediaType.APPLICATION_JSON_VALUE,
    val loginRequestUsernameParameterName: String = "username",
    val loginRequestPasswordParameterName: String = "password"
) {
    companion object {
        val loginExtraAuthenticationTypeConfigClasses = mutableListOf<Class<*>>()
    }
}

