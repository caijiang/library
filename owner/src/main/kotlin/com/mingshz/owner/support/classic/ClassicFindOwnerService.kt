package com.mingshz.owner.support.classic

import com.mingshz.owner.FindOwnerService
import com.mingshz.owner.entity.OwnerEntity
import org.springframework.stereotype.Service
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.servlet.http.HttpServletRequest

/**
 * @author CJ
 */
@Service
class ClassicFindOwnerService : FindOwnerService {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    override fun findOwner(request: HttpServletRequest): OwnerEntity {
        val host = request.serverName
        val cb = entityManager.criteriaBuilder
        val cq = cb.createQuery(OwnerEntity::class.java)
        val root = cq.from(OwnerEntity::class.java)

        val domainPath = root.get<String>("domain")
        return entityManager.createQuery(
            cq.select(root)
                .where(
                    cb.like(cb.literal(host), cb.concat("%", domainPath))
                )
                .orderBy(cb.desc(cb.length(domainPath)))
        )
            .setMaxResults(1)
            .singleResult ?: throw IllegalStateException("bad visit for $host")
    }
}