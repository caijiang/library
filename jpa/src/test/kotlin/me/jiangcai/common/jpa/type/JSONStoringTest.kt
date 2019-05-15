package me.jiangcai.common.jpa.type

import me.jiangcai.common.jpa.JpaTestConfig
import me.jiangcai.common.jpa.entity.Foo
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

/**
 * @author CJ
 */
@ContextConfiguration(classes = [JpaTestConfig::class])
@RunWith(SpringJUnit4ClassRunner::class)
//@Commit
open class JSONStoringTest {
//
//    @Configuration
//    open class Config1 : JpaPackageScanner {
//        override fun addJpaPackage(prefix: String, set: MutableSet<String>) {
//            set.add("me.jiangcai.common.jpa.entity")
//            set.add("org.springframework.data.jpa.convert.threeten")
//        }
//    }

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Test
    @Transactional(readOnly = false)
    open fun go() {
        val foo = Foo()
        val data = mapOf("hello" to "nice")
        foo.goodThing = JSONStoring(
            data
        )


//        val text = objectMapper.writeValueAsString(foo.goodThing)
//        println(text)
//        println(objectMapper.readValue(text,GoodThing::class.java))

        entityManager.persist(foo)
        entityManager.flush()

        println(foo.id)
        val newData = entityManager.find(Foo::class.java, foo.id)
            .goodThing!!.readAs<String, String>()
        assertThat(newData)
            .isEqualTo(data)
    }
}