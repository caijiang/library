package com.mingshz.login

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import java.lang.annotation.Inherited

/**
 * 启用经典身份系统
 * @author CJ
 */
@Suppress("unused")
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention
@MustBeDocumented
@Inherited
//@EnableWebMvc
@ComponentScan("com.mingshz.login.bean")
@Import(ClassicLoginConfigPrefix::class, ClassicLoginConfig::class, ClassicLoginSecurityConfig::class)
annotation class EnableClassicLogin(
    /**
     * 强制通过token授权的token参数名称
     * 通过请求中携带有这个请求参数都可进行强制登录，并且继续业务处理
     */
    val forceAuthenticationTokenParameterName: String = "_token",
    /**
     * 启用经典登录的实体类全限定名称
     */
    val loginClassName: String,
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
)

