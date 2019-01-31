package me.jiangcai.common.jpa

import me.jiangcai.common.jpa.entity.Foo
import me.jiangcai.common.jpa.entity.Foo_
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.WeekFields
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Root

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

            assertThat(
                it.createQuery(
                    cq.select(cb.count(root))
                        .where(
                            b.durationLE(
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
    fun onlyDateFun() {
        runInTx {
            val cb = it.criteriaBuilder
            val b = CriteriaFunctionBuilder(cb).forTimezoneDiff("14:00").build()
            val cq = cb.createQuery(Long::class.java)
            val root = cq.from(Foo::class.java)

            val today = LocalDate.now()

            assertThat(
                it.createQuery(
                    cq
                        .select(cb.count(root))
                        .where(b.dateLessThan(root.get<LocalDateTime>("created"), today, true))
                ).singleResult
            )
                .isEqualTo(0)

            assertThat(
                it.createQuery(
                    cq
                        .select(cb.count(root))
                        .where(b.dateLessThan(root.get(Foo_.created), today, true))
                ).singleResult
            )
                .isEqualTo(0)
        }
    }

    @Test
    fun date() {
        runInTx {
            val f1 = Foo()
            f1.created = LocalDateTime.now()
            it.persist(f1)

            val cb = it.criteriaBuilder
            val b = CriteriaFunctionBuilder(cb).forTimezoneDiff("14:00").build()
            val cq = cb.createQuery(Long::class.java)
            val root = cq.from(Foo::class.java)
            // 按照date查询 今天，1 昨天 0 明天 0

            // 找一个比当时早一秒的时间
            val before = f1.created.minusSeconds(1)

            assertThat(
                it.createQuery(
                    cq
                        .select(cb.count(root))
                        .where(b.dateLE(root.get<LocalDateTime>("created"), before))
                ).singleResult
            )
                .isEqualTo(0)

            assertThat(
                it.createQuery(
                    cq
                        .select(cb.count(root))
                        .where(b.dateLT(root.get<LocalDateTime>("created"), before))
                ).singleResult
            )
                .isEqualTo(0)

            // 找一个比当时晚1秒的时间
            val after = f1.created.plusSeconds(1)

            assertThat(
                it.createQuery(
                    cq
                        .select(cb.count(root))
                        .where(b.dateGE(root.get<LocalDateTime>("created"), after))
                ).singleResult
            )
                .isEqualTo(0)

            assertThat(
                it.createQuery(
                    cq
                        .select(cb.count(root))
                        .where(b.dateGT(root.get<LocalDateTime>("created"), after))
                ).singleResult
            )
                .isEqualTo(0)

            // 已经完成排他测试，现在进行有效测试

            // sum(created > c-1) => 1

            assertThat(
                it.createQuery(
                    cq
                        .select(cb.count(root))
                        .where(b.dateGE(root.get<LocalDateTime>("created"), before))
                ).singleResult
            )
                .isEqualTo(1)

            assertThat(
                it.createQuery(
                    cq
                        .select(cb.count(root))
                        .where(b.dateGT(root.get<LocalDateTime>("created"), before))
                ).singleResult
            )
                .isEqualTo(1)

            // sum(created < c+1) => 1
            assertThat(
                it.createQuery(
                    cq
                        .select(cb.count(root))
                        .where(b.dateLT(root.get<LocalDateTime>("created"), after))
                ).singleResult
            )
                .isEqualTo(1)

            assertThat(
                it.createQuery(
                    cq
                        .select(cb.count(root))
                        .where(b.dateLE(root.get<LocalDateTime>("created"), after))
                ).singleResult
            )
                .isEqualTo(1)

            //
            val today = f1.created.toLocalDate()
            assertThat(
                it.createQuery(
                    cq
                        .select(cb.count(root))
                        .where(b.dateGreaterThan(root.get<LocalDateTime>("created"), today))
                ).singleResult
            )
                .isEqualTo(0)

            assertThat(
                it.createQuery(
                    cq
                        .select(cb.count(root))
                        .where(b.dateGreaterThan(root.get<LocalDateTime>("created"), today, true))
                ).singleResult
            )
                .isEqualTo(1)

            //
            assertThat(
                it.createQuery(
                    cq
                        .select(cb.count(root))
                        .where(b.dateLessThan(root.get<LocalDateTime>("created"), today))
                ).singleResult
            )
                .isEqualTo(0)

            assertThat(
                it.createQuery(
                    cq
                        .select(cb.count(root))
                        .where(b.dateLessThan(root.get<LocalDateTime>("created"), today, true))
                ).singleResult
            )
                .isEqualTo(1)
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

    @Test
    fun query() {
        runInTx { entityManager ->
            // 很简单，一直保存重复的保存,大概持续1年吧。
            val f1 = Foo()
            f1.created = LocalDateTime.now()
            entityManager.persist(f1)
            entityManager.flush()

            // 目标值
            val target = f1.created.plusMonths(1).plusYears(1)
            while (true) {
                if (f1.created > target)
                    break
                f1.created = f1.created.plusDays(1)
                println(f1.created)
                entityManager.merge(f1)

                assertThat(
                    selectPart(entityManager) { cf, root -> cf.year(root.get("created")) }
                )
                    .isEqualTo(f1.created.year)

                assertThat(
                    selectPart(entityManager) { cf, root -> cf.month(root.get("created")) }
                )
                    .isEqualTo(f1.created.monthValue)

                assertThat(
                    selectPart(entityManager) { cf, root -> cf.dayOfMonth(root.get("created")) }
                )
                    .isEqualTo(f1.created.dayOfMonth)
                //
                assertThat(
                    selectPart(entityManager) { cf, root -> cf.weekOfYear(root.get("created"), WeekFields.ISO) }
                )
                    .isEqualTo(f1.created.get(WeekFields.ISO.weekOfWeekBasedYear()))

                assertThat(
                    selectPart(entityManager) { cf, root ->
                        cf.weekOfYear(
                            root.get("created"),
                            WeekFields.SUNDAY_START
                        )
                    }
                )
                    .isEqualTo(f1.created.get(WeekFields.SUNDAY_START.weekOfWeekBasedYear()))

                //
                assertThat(
                    selectPart(entityManager) { cf, root -> cf.yearWeek(root.get("created"), WeekFields.ISO) }
                )
                    .isEqualTo(f1.created.yearWeek(WeekFields.ISO))

                assertThat(
                    selectPart(entityManager) { cf, root ->
                        cf.yearWeek(
                            root.get("created"),
                            WeekFields.SUNDAY_START
                        )
                    }
                )
                    .isEqualTo(f1.created.yearWeek(WeekFields.SUNDAY_START))

            }
        }
    }

    private inline fun <reified T> selectPart(
        entityManager: EntityManager,
        result: ((CriteriaFunction, Root<Foo>) -> Expression<T>)
    ): T {
        val cb = entityManager.criteriaBuilder
        val cq = cb.createQuery(T::class.java)
        val root = cq.from(Foo::class.java)

        val b = CriteriaFunctionBuilder(cb).forTimezoneDiff("14:00").build()

        return entityManager.createQuery(cq.select(result(b, root))).singleResult
    }

}