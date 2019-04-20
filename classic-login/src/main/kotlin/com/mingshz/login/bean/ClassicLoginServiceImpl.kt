package com.mingshz.login.bean

import com.mingshz.login.ClassicLoginConfig
import com.mingshz.login.ClassicLoginService
import com.mingshz.login.entity.Login
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import javax.persistence.EntityManager
import javax.persistence.NoResultException
import javax.persistence.PersistenceContext

/**
 * @author CJ
 */
@Suppress("UNCHECKED_CAST")
@Service
class ClassicLoginServiceImpl<T : Login>(
    @Autowired
    private val classicPasswordEncoder: PasswordEncoder
) : ClassicLoginService<T> {

    override fun newLogin(login: T, rawPassword: String): T {
        login.enabled = true
        login.password = classicPasswordEncoder.encode(rawPassword)
        entityManager.persist(login)
        return login
    }

    @PersistenceContext
    private lateinit var entityManager: EntityManager

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
}