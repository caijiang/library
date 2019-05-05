package com.mingshz.login.bean

import com.mingshz.login.ClassicLoginConfig
import com.mingshz.login.ClassicLoginService
import com.mingshz.login.entity.Login
import com.mingshz.login.entity.LoginToken
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.temporal.TemporalUnit
import javax.persistence.EntityManager
import javax.persistence.EntityNotFoundException
import javax.persistence.NoResultException
import javax.persistence.PersistenceContext

/**
 * @author CJ
 */
@Suppress("UNCHECKED_CAST")
@Service
open class ClassicLoginServiceImpl<T : Login>(
    @Autowired
    private val classicPasswordEncoder: PasswordEncoder
) : ClassicLoginService<T> {

    override fun <X : T> newLogin(login: X, rawPassword: String?): X {
        login.enabled = true
        rawPassword?.let {
            login.password = classicPasswordEncoder.encode(it)
        }
        entityManager.persist(login)
        return login
    }

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    override fun findLogin(id: Long): T {
        val type: Class<T> = Class.forName(ClassicLoginConfig.loginClassName!!) as Class<T>
        return entityManager.find(type, id) ?: throw UsernameNotFoundException("can not find $id")
    }

    override fun loadUserByUsername(username: String?): T {
        if (username.isNullOrEmpty())
            throw UsernameNotFoundException("null username")
        val type: Class<T> = Class.forName(ClassicLoginConfig.loginClassName!!) as Class<T>

        val cb = entityManager.criteriaBuilder
        val cq = cb.createQuery(type)
        val root = cq.from(type)

        return try {
            entityManager.createQuery(
                cq.select(root)
                    .where(cb.equal(root.get<String>("username"), username))
            )
                .singleResult
        } catch (e: NoResultException) {
            throw UsernameNotFoundException("can not find $username")
        }
    }

    override fun requestToken(
        login: T,
        expireUnit: TemporalUnit,
        expireLong: Long,
        uriBuilder: StringBuilder?
    ): String {
        val t = LoginToken(target = login)
        if (expireLong > 0) {
            t.expireTime = t.createTime.plus(expireLong, expireUnit)
        }
        entityManager.persist(t)

        val fullToken = t.getFullToken()
        uriBuilder?.let {


            if (!uriBuilder.contains("?")) {
                // 如果是什么都没有的
                uriBuilder.append("?${ClassicLoginConfig.forceAuthenticationTokenParameterName}=$fullToken")
            } else if (!uriBuilder.contains("=") && uriBuilder.endsWith("?")) {
                //如果仅仅包含? 但是没有携带任何参数 比如 /hello?
                uriBuilder.append("${ClassicLoginConfig.forceAuthenticationTokenParameterName}=$fullToken")
            } else if (uriBuilder.endsWith("&")) {
                // 如果后面已经有一个准备好的 &
                uriBuilder.append("${ClassicLoginConfig.forceAuthenticationTokenParameterName}=$fullToken")
            } else {
                uriBuilder.append("&${ClassicLoginConfig.forceAuthenticationTokenParameterName}=$fullToken")
            }
        }
        return fullToken
    }

    override fun loadUserDetails(token: PreAuthenticatedAuthenticationToken): T {

        val tk = try {
            entityManager.getReference(LoginToken::class.java, token.credentials.toString())
        } catch (e: EntityNotFoundException) {
            throw UsernameNotFoundException("bad token $token")
        }
        // 检查有效期
        if (tk.expireTime == null)
            return (tk.target as T?)!!

        if (LocalDateTime.now() > tk.expireTime) {
            throw UsernameNotFoundException("$token has expired.")
        }
        return (tk.target as T?)!!
    }

    override fun changePassword(loginId: Long, originPassword: String, newPassword: String) {
        val login = findLogin(loginId)
        if (!classicPasswordEncoder.matches(originPassword, login.password))
            throw IllegalArgumentException("密码不正确。")

        login.password = classicPasswordEncoder.encode(newPassword)
    }
}