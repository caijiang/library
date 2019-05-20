package me.jiangcai.crud.controller

import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import java.io.Serializable

/**
 * 所需权利
 * @author CJ
 */
@Suppress("unused")
interface Right {
    /**
     * 检查权限
     *
     * @param authentication 鉴权
     * @param principal 身份
     * @param id 已确定的资源ID
     * @param allPathVariables 所有的[org.springframework.web.bind.annotation.PathVariable]
     * @param entity 已确定的资源
     * @throws AccessDeniedException 权限不足时
     */
    @Throws(AccessDeniedException::class)
    fun <ID : Serializable> check(
        authentication: Authentication?,
        principal: Any?,
        id: ID?,
        allPathVariables: Map<String, String>,
        entity: Any?
    )

    companion object {
        /**
         * 允许所有的所需权利
         */
        fun withPermitAll(): Right? = null

        /**
         * 拒绝所有的所需权利
         */
        fun withDenyAll(): Right = DenyAllRight()

        /**
         * 只要有以上权利即可
         */
        fun withRoles(vararg roles: String): Right = RolesRight(roles)

        /**
         * 要求非匿名
         */
        fun withNonAnonymous(): Right = AnonymousRight(true)

        /**
         * 要求匿名
         */
        fun withAnonymous(): Right = AnonymousRight(false)
    }
}

/**
 * 匿名权利
 */
class AnonymousRight(
    /**
     * 不允许匿名
     */
    private val no: Boolean
) : Right {
    override fun <ID : Serializable> check(
        authentication: Authentication?,
        principal: Any?,
        id: ID?,
        allPathVariables: Map<String, String>,
        entity: Any?
    ) {
        if (authentication == null)
            throw AccessDeniedException("")
        if (authentication.isAuthenticated && !no) {
            throw AccessDeniedException("anonymous required, but authenticated.")
        }
        if (!authentication.isAuthenticated && no) {
            throw AccessDeniedException("anonymous required, but authenticated.")
        }
    }

}

class RolesRight(
    private val roles: Array<out String>
) : Right {
    override fun <ID : Serializable> check(
        authentication: Authentication?,
        principal: Any?,
        id: ID?,
        allPathVariables: Map<String, String>,
        entity: Any?
    ) {
        if (authentication == null)
            throw AccessDeniedException("")
        if (!authentication.authorities.any { authority ->
                roles.any {
                    authority.authority.removePrefix("ROLE_") == it.removePrefix("ROLE_")
                }
            }) throw AccessDeniedException("")
    }

}

class DenyAllRight : Right {
    override fun <ID : Serializable> check(
        authentication: Authentication?,
        principal: Any?,
        id: ID?,
        allPathVariables: Map<String, String>,
        entity: Any?
    ) {
        throw AccessDeniedException("")
    }

}