@file:Suppress("unused")

package me.jiangcai.common.ext.help

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails

/**
 * 运行一段越权block
 */
fun <R> runAsUserDetails(details: UserDetails, block: () -> R): R {
    val pre = SecurityContextHolder.getContext()

    try {
        val context = SecurityContextHolder.createEmptyContext()
        context.authentication = object : Authentication {
            override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
                return details.authorities
            }

            override fun setAuthenticated(isAuthenticated: Boolean) {
            }

            override fun getName(): String {
                return details.username
            }

            override fun getCredentials(): Any {
                return details
            }

            override fun getPrincipal(): Any {
                return details
            }

            override fun isAuthenticated(): Boolean {
                return true
            }

            override fun getDetails(): Any {
                return details
            }

        }
        SecurityContextHolder.setContext(context)
        return block()
    } finally {
        SecurityContextHolder.setContext(pre)
    }

}

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