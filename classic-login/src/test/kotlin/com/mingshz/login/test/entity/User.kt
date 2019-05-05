package com.mingshz.login.test.entity

import com.mingshz.login.entity.Login
import org.springframework.security.core.GrantedAuthority
import java.util.*
import javax.persistence.Entity

/**
 * @author CJ
 */
@Entity
class User : Login() {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return Collections.emptyList()
    }
}