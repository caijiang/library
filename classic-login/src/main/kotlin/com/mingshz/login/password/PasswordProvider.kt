package com.mingshz.login.password

import com.mingshz.login.ClassicLoginService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * @author CJ
 */
class PasswordProvider(
    @Autowired
    classicsPasswordEncoder: PasswordEncoder,
    @Autowired
    classicLoginService: ClassicLoginService<*>
) : DaoAuthenticationProvider() {

    init {
        setPasswordEncoder(classicsPasswordEncoder)
        userDetailsService = classicLoginService
    }
}