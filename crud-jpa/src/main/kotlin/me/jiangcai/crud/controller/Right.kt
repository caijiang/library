package me.jiangcai.crud.controller

import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import java.io.Serializable

/**
 * 所需权利
 * @author CJ
 */
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
        fun WithPermitAll(): Right? = null

        /**
         * 拒绝所有的所需权利
         */
        fun WithDenyAll(): Right = DenyAllRight()

        /**
         * 只要有以上权利即可
         */
        fun WithRoles(vararg roles: String) = RolesRight(roles)

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