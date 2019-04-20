package com.mingshz.login

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * @author CJ
 */
@Configuration
open class ClassicLoginConfigPrefix {

    /**
     * 一个经典的加密器
     */
    @Bean
    open fun classicPasswordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}