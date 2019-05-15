package com.mingshz.owner.support

import com.mingshz.owner.FindOwnerService
import com.mingshz.owner.entity.OwnerEntity
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import javax.annotation.PostConstruct
import javax.persistence.EntityManager
import javax.persistence.NoResultException
import javax.persistence.PersistenceContext
import javax.servlet.http.HttpServletRequest

/**
 * @author CJ
 */
open class SingleFindOwnerService : FindOwnerService {

    private lateinit var owner: OwnerEntity

    lateinit var ownerEntity: OwnerEntity
    private val log = LogFactory.getLog(SingleFindOwnerService::class.java)

    @PersistenceContext
    private lateinit var entityManager: EntityManager
    @Suppress("SpringJavaAutowiredMembersInspection")
    @Autowired
    private lateinit var transactionManager: PlatformTransactionManager

    //    @Transactional
    @PostConstruct
    open fun init() {
        log.debug("初始化单业主系统，如果本地没有则创造一个。")
        val cb = entityManager.criteriaBuilder
        val cq = cb.createQuery(OwnerEntity::class.java)
        val root = cq.from(OwnerEntity::class.java)
        owner = try {
            entityManager
                .createQuery(
                    cq.select(root)
                        .where(cb.equal(root.get<String>("alias"), ownerEntity.alias))
                )
                .singleResult
        } catch (e: NoResultException) {
            TransactionTemplate(transactionManager)
                .execute {
                    entityManager.persist(ownerEntity)
                    entityManager.flush()
                    ownerEntity
                }
        }
    }


    // 根据委托系统 进行更新？
    override fun findOwner(request: HttpServletRequest): OwnerEntity = owner
}