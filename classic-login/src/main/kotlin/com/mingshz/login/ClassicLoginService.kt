package com.mingshz.login

import com.mingshz.login.entity.Login
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.transaction.annotation.Transactional

/**
 * @author CJ
 */
interface ClassicLoginService<T : Login> : UserDetailsService {


    @Transactional(readOnly = true)
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String?): T

    /**
     * 保存一个新用户
     * @param rawPassword 明文密码
     * @return 新用户
     */
    @Transactional
    fun newLogin(login: T, rawPassword: String): T
}