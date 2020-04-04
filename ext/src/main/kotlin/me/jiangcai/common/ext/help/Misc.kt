@file:Suppress("unused")

package me.jiangcai.common.ext.help

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder

/**
 * 运行一段越权block
 */
fun <R> runAsRoot(block: () -> R): R {
    val pre = SecurityContextHolder.getContext()

    try {
        val context = SecurityContextHolder.createEmptyContext()
        context.authentication = object : Authentication {
            override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
                return mutableListOf(SimpleGrantedAuthority("ROLE_ROOT"))
            }

            override fun setAuthenticated(isAuthenticated: Boolean) {
            }

            override fun getName(): String {
                return "root"
            }

            override fun getCredentials(): Any {
                return "root"
            }

            override fun getPrincipal(): Any {
                return "root"
            }

            override fun isAuthenticated(): Boolean {
                return true
            }

            override fun getDetails(): Any {
                return "root"
            }

        }
        SecurityContextHolder.setContext(context)
        return block()
    } finally {
        SecurityContextHolder.setContext(pre)
    }

}