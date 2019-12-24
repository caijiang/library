package me.jiangcai.common.jpa

import me.jiangcai.common.jpa.entity.Foo
import me.jiangcai.common.jpa.entity.Foo_
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.criteria.Expression
import javax.persistence.criteria.JoinType
import javax.persistence.criteria.Root
import kotlin.math.ceil
import kotlin.random.Random

/**
 * https://www.baeldung.com/junit-5-migration
 * @author CJ
 */
@ContextConfiguration(classes = [JpaTestConfig::class])
@RunWith(SpringJUnit4ClassRunner::class)
abstract class CriteriaFunctionBuilderTest {

    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private lateinit var entityManagerFactory: EntityManagerFactory
//    @Autowired
//    private lateinit var dataSource: DataSource

//    @Test
//    @Ignore
//    fun jdbc() {
//        // jdbc ……
//        // 插入一个普通时间？ cst?
//        // Date.from(source.atZone(systemDefault()).toInstant())
//        val template = JdbcTemplate(dataSource)
//        template.execute("DELETE FROM foo")
//        template.execute("INSERT INTO foo(`created`) VALUES(?)") {
//            //            it.setDate(1, Date(System.currentTimeMillis()))
//            it.setTimestamp(1, Timestamp.from(java.util.Date(System.currentTimeMillis()).toInstant()))
//            it.executeUpdate()
//        }
//        template.execute("INSERT INTO foo(`created`) VALUES(?)") {
//            //            it.setDate(1, Date(Date.from(LocalDateTime.now().atZone(systemDefault()).toInstant()).time))
//            it.setTimestamp(1, Timestamp.from(LocalDateTime.now().atZone(systemDefault()).toInstant()))
//            it.executeUpdate()
//        }
//    }


    private fun runInTx(commit: Boolean = false, work: (EntityManager) -> Unit) {
        val entityManager = entityManagerFactory.createEntityManager()
        try {
            entityManager.transaction.begin()


            work(entityManager)


        } finally {
            if (commit)
                entityManager.transaction.commit()
            else
                entityManager.transaction.rollback()
            entityManager.close()
        }
    }

    @Test
    fun numeric() {
        runInTx {
            val f1 = Foo()
            it.persist(f1)
            it.flush()

            val random = Random(System.currentTimeMillis())
            for (i in 1..50) {
                f1.value = random.nextDouble((-100).toDouble(), (100).toDouble()).toBigDecimal()
                    .setScale(10, RoundingMode.DOWN)
                it.merge(f1)

                assertThat(selectPart(it) { cf, root ->
                    cf.ceil(root.get(Foo_.value))
                })
                    .isEqualTo(Math.ceil(f1.value!!.toDouble()).toInt())

                assertThat(selectPart(it) { cf, root ->
                    cf.floor(root.get(Foo_.value))
                })
                    .isEqualTo(Math.floor(f1.value!!.toDouble()).toInt())

                // exp 太大了 不好测试
                @Suppress("ConstantConditionIf")
                if (false)
                    assertThat(selectPart(it) { cf, root ->
                        cf.exp(root.get(Foo_.value))
                    })
                        .isCloseTo(Math.exp(f1.value!!.toDouble()).toBigDecimal(), Offset.offset(BigDecimal("0.1")))
            }
        }
    }

    @Test
    fun duration() {
        runInTx {
            val f1 = Foo()
            f1.created = LocalDateTime.now()
            it.persist(f1)

            val cb = it.criteriaBuilder
            val b = CriteriaFunctionBuilder(cb)
                .forTimezoneDiff(timezoneDiff)
                .forEntityManager(it)
                .build()
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
            val b = CriteriaFunctionBuilder(cb)
                .forEntityManager(it)
                .forTimezoneDiff(timezoneDiff).build()
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
            val b = CriteriaFunctionBuilder(cb)
                .forEntityManager(it)
                .forTimezoneDiff(timezoneDiff).build()
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
            val b = CriteriaFunctionBuilder(cb)
                .forEntityManager(it)
                .forTimezoneDiff(timezoneDiff).build()
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
            val b = CriteriaFunctionBuilder(cb)
                .forEntityManager(it)
                .forTimezoneDiff(timezoneDiff).build()
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
    @Ignore
    fun groupConcat() {
        runInTx { entityManager ->
            val f1 = Foo()
            entityManager.persist(f1)
            entityManager.flush()

            val cb = entityManager.criteriaBuilder
//            val b = CriteriaFunctionBuilder(cb).build()
            val cq = cb.createTupleQuery()
            val root = cq.from(Foo::class.java)

            assertThat(
                entityManager.createQuery(
                    cq
                        .multiselect(
                            root.get<Long>("id"),
                            cb.groupConcat(root.joinList<Foo, String>("tags", JoinType.LEFT))
                        )
//                        .where(cb.equal(root.get<Long>("id"), f1.id))
                        .groupBy(root.get<Long>("id"))
                ).setMaxResults(1).singleResult
                    .get(1)
            )

                .isNull()

            f1.tags = listOf("a").toMutableList()
            entityManager.merge(f1)

            assertThat(
                entityManager.createQuery(
                    cq
                        .multiselect(
                            root.get<Long>("id"),
                            cb.groupConcat(root.joinList<Foo, String>("tags", JoinType.LEFT))
                        )
                        .where(cb.equal(root.get<Long>("id"), f1.id))
                        .groupBy(root.get<Long>("id"))
                ).setMaxResults(1).singleResult
                    .get(1)
            )
                .isEqualTo("a")

            f1.tags = listOf("a", "b", "c").toMutableList()
            entityManager.merge(f1)

            assertThat(
                entityManager.createQuery(
                    cq
                        .multiselect(
                            root.get<Long>("id"),
                            cb.groupConcat(root.joinList<Foo, String>("tags", JoinType.LEFT))
                        )
                        .where(cb.equal(root.get<Long>("id"), f1.id))
                        .groupBy(root.get<Long>("id"))
                ).setMaxResults(1).singleResult
                    .get(1)
            )
                .isEqualTo("a,b,c")
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

            val random = Random(System.currentTimeMillis())

            // 目标值
            val target = f1.created.plusMonths(1).plusYears(1)
            while (true) {
                if (f1.created > target)
                    break
                f1.created = f1.created.plusDays(1)
                    .plus(random.nextLong(1, 60L * 60L * 1000000L), ChronoUnit.MICROS) // 最多 产生1h的变化 也就是
                // 把微妙数清空
                f1.created = f1.created.minus(f1.created.get(ChronoField.MICRO_OF_SECOND).toLong(), ChronoUnit.MICROS)
                println(f1.created)
                entityManager.merge(f1)

                // 时刻
                assertThat(
                    selectPart(entityManager) { cf, root -> cf.hour(root.get("created")) }
                )
                    // 这里也会有误差，但概率很低 ， 59,59 才会出现
                    .isEqualTo(f1.created.hour)
                assertThat(
                    selectPart(entityManager) { cf, root -> cf.minute(root.get("created")) }
                )
                    // 因为数据库系统会默认进行四舍五入，所以会有1秒的误差
                    .isCloseTo(f1.created.minute, Offset.offset(1))
                assertThat(
                    selectPart(entityManager) { cf, root -> cf.second(root.get("created")) }
                )
                    // 因为数据库系统会默认进行四舍五入，所以会有1秒的误差
                    .isCloseTo(f1.created.second, Offset.offset(1))

                assertThat(
                    selectPart(entityManager) { cf, root -> cf.year(root.get("created")) }
                )
                    .isEqualTo(f1.created.year)

                assertThat(
                    selectPart(entityManager) { cf, root -> cf.month(root.get("created")) }
                )
                    .isEqualTo(f1.created.monthValue)

//                println(selectPart(entityManager) { cf, root -> cf.builder.quot(cf.month(root.get("created")).`as`(BigDecimal::class.java),BigDecimal.valueOf(3.0)) })
                assertThat(
                    selectPart(entityManager) { cf, root -> cf.quarter(root.get("created")) }
                )
                    .isEqualTo(ceil(f1.created.monthValue.toDouble() / (3).toDouble()).toInt())

                assertThat(
                    selectPart(entityManager) { cf, root -> cf.dayOfMonth(root.get("created")) }
                )
                    .isEqualTo(f1.created.dayOfMonth)
                //
                assertThat(
                    selectPart(entityManager) { cf, root -> cf.weekOfYear(root.get("created"), WeekFields.ISO) }
                )
                    .isEqualTo(f1.created.get(WeekFields.ISO.weekOfWeekBasedYear()))

                // TODO start query weekOfYear
//                assertThat(
//                    selectPart(entityManager) { cf, root ->
//                        cf.weekOfYear(
//                            root.get("created"),
//                            WeekFields.SUNDAY_START
//                        )
//                    }
//                )
//                    .isEqualTo(f1.created.get(WeekFields.SUNDAY_START.weekOfWeekBasedYear()))

                //
                assertThat(
                    selectPart(entityManager) { cf, root -> cf.yearWeek(root.get("created"), WeekFields.ISO) }
                )
                    .isEqualTo(f1.created.yearWeek(WeekFields.ISO))

                // TODO start query yearWeek
//                assertThat(
//                    selectPart(entityManager) { cf, root ->
//                        cf.yearWeek(
//                            root.get("created"),
//                            WeekFields.SUNDAY_START
//                        )
//                    }
//                )
//                    .isEqualTo(f1.created.yearWeek(WeekFields.SUNDAY_START))

            }
        }
    }

    private val timezoneDiff = "00:00"

    private inline fun <reified T> selectPart(
        entityManager: EntityManager,
        result: ((CriteriaFunction, Root<Foo>) -> Expression<T>)
    ): T {
        val cb = entityManager.criteriaBuilder
        val cq = cb.createQuery(T::class.java)
        val root = cq.from(Foo::class.java)


        val b = CriteriaFunctionBuilder(cb)
            .forEntityManager(entityManager)
            .forTimezoneDiff(timezoneDiff).build()

        return entityManager.createQuery(cq.select(result(b, root))).singleResult
    }

}