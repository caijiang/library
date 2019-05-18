package me.jiangcai.crud.env.entity2

import com.mingshz.login.entity.Login
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import javax.persistence.ElementCollection
import javax.persistence.Entity

/**
 * @author CJ
 */
@Entity
class User(
    @ElementCollection
    var authorities: Set<String> = emptySet()
) : Login() {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
        authorities.map {
            SimpleGrantedAuthority("ROLE_$it")
        }
            .toMutableSet()
}