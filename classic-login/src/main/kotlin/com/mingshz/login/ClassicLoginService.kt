package com.mingshz.login

import com.mingshz.login.entity.Login
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.transaction.annotation.Transactional
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit

/**
 * 提供登录相关的服务
 * @author CJ
 */
interface ClassicLoginService<T : Login> : UserDetailsService,
    AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

    /**
     * 根据id查找身份，
     * @return never null 代替的是[UsernameNotFoundException]
     */
    @Throws(UsernameNotFoundException::class)
    @Transactional(readOnly = true)
    fun findLogin(id: Long): T

    @Transactional(readOnly = true)
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String?): T

    @Transactional(readOnly = true)
    @Throws(UsernameNotFoundException::class)
    override fun loadUserDetails(token: PreAuthenticatedAuthenticationToken): T

    /**
     * 保存一个新用户
     * @param rawPassword 明文密码，如果不传值则表示不允许用户以密码登录
     * @return 新用户
     */
    @Transactional
    fun <X : T> newLogin(login: X, rawPassword: String? = null): X

    /**
     * 为这个用户请求快速登录token
     * @param login 用户
     * @param expireUnit 过期时间单位
     * @param expireLong 过期时间长度 如果是-1 则表示永不过期
     * @param uriBuilder 可选的，如果存在的话 会将参数写入到这个builder中
     * @return token文本
     */
    @Transactional
    fun requestToken(
        login: T,
        expireUnit: TemporalUnit = ChronoUnit.SECONDS,
        expireLong: Long = -1,
        uriBuilder: StringBuilder? = null
    ): String
}