package me.jiangcai.common.bs.bean

import me.jiangcai.common.bs.BusinessSafe
import me.jiangcai.common.bs.ClassicService
import me.jiangcai.common.bs.entity.SimpleRow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceContext
import kotlin.random.Random

/**
 * @author CJ
 */
@Service
open class ClassicServiceImpl(
    @Autowired
    private val entityManagerFactory: EntityManagerFactory
) : ClassicService {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Transactional
//    @PostConstruct
    override fun init() {
        workWithSimple("two", "")
        workWithSimple("one", "")
    }

    @BusinessSafe
    @Transactional
    override fun addNameToSimpleInLock(name: String): String {
        return workWithSimple("two", name)
    }

    @Transactional
    override fun addNameToSimple(name: String): String {
        return workWithSimple("one", name)
    }

    private fun workWithSimple(id: String, name: String): String {
//        val x = TransactionSynchronizationManager.getResource(entityManagerFactory)
//        val entityManager = if (x != null && x is EntityManagerHolder) x.entityManager
//        else entityManagerFactory.createEntityManager()


        var sr = entityManager.find(SimpleRow::class.java, id)
        return if (sr == null) {
            sr = SimpleRow()
            sr.id = id
            sr.value = name
            entityManager.persist(sr)
            sr.value
        } else {
            sr.value = sr.value + name
            entityManager.merge(sr)
            sr.value
        }
    }

    private var memory2: String = ""

    @BusinessSafe
    override fun contactWithMemoryInLock(name: String): String {
        // 模拟业务性质，比如存在io啊什么的
        Thread.sleep(Random(System.currentTimeMillis()).nextLong(100, 500))
        memory2 += name
        return memory2
    }

    private var memory: String = ""
    override fun contactWithMemory(name: String): String {
        // 模拟业务性质，比如存在io啊什么的
        Thread.sleep(Random(System.currentTimeMillis()).nextLong(100, 500))
        memory += name
        return memory
    }

}