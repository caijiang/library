package me.jiangcai.common.jpa

import me.jiangcai.common.jpa.entity.Foo
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory

/**
 * https://www.baeldung.com/junit-5-migration
 * @author CJ
 */
@ContextConfiguration(classes = [JpaTestConfig::class])
@RunWith(SpringJUnit4ClassRunner::class)
class CriteriaFunctionBuilderTest {

    @Autowired
    private lateinit var entityManagerFactory: EntityManagerFactory

    private fun runInTx(work: (EntityManager) -> Unit) {
        val entityManager = entityManagerFactory.createEntityManager()
        try {
            entityManager.transaction.begin()


            work(entityManager)


        } finally {
            entityManager.transaction.rollback()
            entityManager.close()
        }
    }

    @Test
    fun duration() {
        runInTx {
            val f1 = Foo()
            f1.created = LocalDateTime.now()
            it.persist(f1)

            val cb = it.criteriaBuilder
            val b = CriteriaFunctionBuilder(cb).forTimezoneDiff("14:00").build()
            val cq = cb.createQuery(Long::class.java)
            val root = cq.from(Foo::class.java)

            assertThat(
                it.createQuery(
                    cq.select(cb.count(root))
                        .where(
                            b.durationGE(
                                root.get<LocalDateTime>("created"), root.get<LocalDateTime>("created"),
                                Duration.ofSeconds(0)
                            ), b.durationLE(
                                root.get<LocalDateTime>("created"), root.get<LocalDateTime>("created"),
                                Duration.ofSeconds(0)
                            )
                        )
                ).singleResult
            )
                .isEqualTo(1)

            assertThat(
                it.createQuery(
                    cq.select(cb.count(root))
                        .where(
                            b.durationGE(
                                root.get<LocalDateTime>("created"), f1.created,
                                Duration.ofSeconds(-1)
                            )
                        )
                ).singleResult
            )
                .isEqualTo(1)

            assertThat(
                it.createQuery(
                    cq.select(cb.count(root))
                        .where(
                            b.durationGT(
                                root.get<LocalDateTime>("created"), root.get<LocalDateTime>("created"),
                                Duration.ofSeconds(0)
                            )
                        )
                ).singleResult
            )
                .isEqualTo(0)

            assertThat(
                it.createQuery(
                    cq.select(cb.count(root))
                        .where(
                            b.durationLT(
                                root.get<LocalDateTime>("created"), root.get<LocalDateTime>("created"),
                                Duration.ofSeconds(0)
                            )
                        )
                ).singleResult
            )
                .isEqualTo(0)


            assertThat(
                it.createQuery(
                    cq.select(cb.count(root))
                        .where(
                            b.durationGT(
                                root.get<LocalDateTime>("created"), f1.created,
                                Duration.ofSeconds(1)
                            )
                        )
                ).singleResult
            )
                .isEqualTo(0)

            assertThat(
                it.createQuery(
                    cq.select(cb.count(root))
                        .where(
                            b.durationLT(
                                root.get<LocalDateTime>("created"), f1.created,
                                Duration.ofSeconds(1)
                            )
                        )
                ).singleResult
            )
                .isEqualTo(1)
        }
    }

    @Test
    fun yearAndMonthEqual() {
        runInTx {
            val f1 = Foo()
            f1.created = LocalDateTime.now()
            it.persist(f1)

            val cb = it.criteriaBuilder
            val b = CriteriaFunctionBuilder(cb).forTimezoneDiff("14:00").build()
            val cq = cb.createQuery(Long::class.java)
            val root = cq.from(Foo::class.java)

            assertThat(
                it.createQuery(
                    cq
                        .select(cb.count(root))
                        .where(b.yearAndMonthEqual(root.get<LocalDateTime>("created"), LocalDate.now()))
                ).singleResult
            )
                .isEqualTo(1)

            assertThat(
                it.createQuery(
                    cq
                        .select(cb.count(root))
                        .where(
                            b.yearAndMonthEqual(
                                root.get<LocalDateTime>("created"),
                                root.get<LocalDateTime>("created")
                            )
                        )
                ).singleResult
            )
                .isEqualTo(1)


            assertThat(
                it.createQuery(
                    cq
                        .select(cb.count(root))
                        .where(b.yearAndMonthEqual(root.get<LocalDateTime>("created"), LocalDate.now().plusMonths(1)))
                ).singleResult
            )
                .isEqualTo(0)

            assertThat(
                it.createQuery(
                    cq
                        .select(cb.count(root))
                        .where(b.yearAndMonthEqual(root.get<LocalDateTime>("created"), LocalDate.now().minusMonths(1)))
                ).singleResult
            )
                .isEqualTo(0)

            assertThat(
                it.createQuery(
                    cq
                        .select(cb.count(root))
                        .where(
                            b.yearAndMonthEqual(
                                root.get<LocalDateTime>("created"),
                                f1.created.year,
                                f1.created.month
                            )
                        )
                ).singleResult
            )
                .isEqualTo(1)

            assertThat(
                it.createQuery(
                    cq
                        .select(cb.count(root))
                        .where(
                            b.yearAndMonthEqual(
                                root.get<LocalDateTime>("created"),
                                f1.created.year,
                                f1.created.monthValue
                            )
                        )
                ).singleResult
            )
                .isEqualTo(1)

            assertThat(
                it.createQuery(
                    cq
                        .select(cb.count(root))
                        .where(
                            b.yearAndMonthEqual(
                                root.get<LocalDateTime>("created"),
                                f1.created.year,
                                f1.created.plusMonths(1).month
                            )
                        )
                ).singleResult
            )
                .isEqualTo(0)

            assertThat(
                it.createQuery(
                    cq
                        .select(cb.count(root))
                        .where(
                            b.yearAndMonthEqual(
                                root.get<LocalDateTime>("created"),
                                f1.created.year,
                                f1.created.plusMonths(1).monthValue
                            )
                        )
                ).singleResult
            )
                .isEqualTo(0)


        }
    }

    @Test
    fun dateEqual() {
        runInTx {
            val f1 = Foo()
            f1.created = LocalDateTime.now()
            it.persist(f1)

            val cb = it.criteriaBuilder
            val b = CriteriaFunctionBuilder(cb).forTimezoneDiff("14:00").build()
            val cq = cb.createQuery(Long::class.java)
            val root = cq.from(Foo::class.java)
            // 按照date查询 今天，1 昨天 0 明天 0

            assertThat(
                it.createQuery(
                    cq
                        .select(cb.count(root))
                        .where(b.dateEqual(root.get<LocalDateTime>("created"), LocalDate.now()))
                ).singleResult
            )
                .isEqualTo(1)

            assertThat(
                it.createQuery(
                    cq
                        .select(cb.count(root))
                        .where(b.dateEqual(root.get<LocalDateTime>("created"), root.get<LocalDateTime>("created")))
                ).singleResult
            )
                .isEqualTo(1)

            assertThat(
                it.createQuery(
                    cq
                        .select(cb.count(root))
                        .where(b.dateEqual(root.get<LocalDateTime>("created"), LocalDate.now().plusDays(1)))
                ).singleResult
            )
                .isEqualTo(0)

            assertThat(
                it.createQuery(
                    cq
                        .select(cb.count(root))
                        .where(b.dateEqual(root.get<LocalDateTime>("created"), LocalDate.now().minusDays(1)))
                ).singleResult
            )
                .isEqualTo(0)

        }
    }

    @Test
    fun contact() {
        runInTx { entityManager ->
            val f1 = Foo()
            entityManager.persist(f1)
            entityManager.flush()

            val cb = entityManager.criteriaBuilder
            val b = CriteriaFunctionBuilder(cb).build()
            val cq = cb.createQuery(String::class.java)
            cq.from(Foo::class.java)

            assertThat(
                entityManager.createQuery(
                    cq
                        .select(b.contact(cb.literal("hello")))
                )
                    .setMaxResults(1)
                    .singleResult
            )
                .isEqualTo("hello")

            assertThat(
                entityManager.createQuery(
                    cq
                        .select(b.contact(cb.literal("hello"), cb.literal(" world")))
                ).setMaxResults(1).singleResult
            )
                .isEqualTo("hello world")

            assertThat(
                entityManager.createQuery(
                    cq
                        .select(b.contact(cb.literal("hello"), cb.literal(" world"), cb.literal(" and you")))
                ).setMaxResults(1).singleResult
            )
                .isEqualTo("hello world and you")

            assertThat(
                entityManager.createQuery(
                    cq
                        .select(b.contact())
                ).setMaxResults(1).singleResult
            )
                .isNull()
        }

    }

}