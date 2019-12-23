package me.jiangcai.common.jpa

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

/**
 * @author CJ
 */
@RunWith(SpringJUnit4ClassRunner::class)
class EnableJpaTest {

    @PersistenceContext(unitName = "persistenceUnitName")
    private lateinit var entityManager: EntityManager
    @PersistenceContext(unitName = "wellPersistenceUnitName")
    private lateinit var entityManager2: EntityManager

    @Test
    fun go() {
        assertThat(entityManager).isNotNull
        assertThat(entityManager2).isNotNull
    }

    @Configuration
    open class Config1 : JpaPackageScanner {
        override fun addJpaPackage(prefix: String, set: MutableSet<String>) {
            set.add("org.springframework.data.jpa.convert.threeten")
        }
    }

    @EnableJpa(prefix = "well", useH2TempDataSource = true)
    @Configuration
    open class Config2 : JpaPackageScanner {
        override fun addJpaPackage(prefix: String, set: MutableSet<String>) {
            if ("well" == prefix) {
                set.add("me.jiangcai.common.jpa.entity")
            }
        }

    }

    @EnableJpa(useH2TempDataSource = true)
    @Configuration
    open class Config : JpaTestConfig()
}