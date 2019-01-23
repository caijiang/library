package me.jiangcai.common.bs

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore
import kotlin.random.Random

/**
 * 单个测试就比较简单。
 *
 * @author CJ
 */
@RunWith(SpringJUnit4ClassRunner::class)
@ContextConfiguration(classes = [MakeBusinessSafeConfigTest::class])
class SingleLockerTest {

    @Autowired
    private lateinit var classicService: ClassicService

    @Test
    fun go() {
        // 普通的方法，必然并发混乱
        concurrentLogic({
            classicService.contactWithMemory(it)
        }, {
            assertThat(classicService.contactWithMemory(""))
                .isNotEqualTo(it)
        })

        // 安全的方法，值得依赖
        concurrentLogic({
            classicService.contactWithMemoryInLock(it)
        }, {
            assertThat(classicService.contactWithMemoryInLock(""))
                .isEqualTo(it)
        })

        // 和数据库事务的联合执法
    }

    @Test
    fun withTx() {

        classicService.init()

        // 普通的方法，必然并发混乱
        concurrentLogic({
            classicService.addNameToSimple(it)
        }, {
            assertThat(classicService.addNameToSimple(""))
                .isNotEqualTo(it)
        })

        // 安全的方法，值得依赖
        concurrentLogic({
            classicService.addNameToSimpleInLock(it)
        }, {
            assertThat(classicService.addNameToSimpleInLock(""))
                .isEqualTo(it)
        })

        // 和数据库事务的联合执法
    }

    /**
     * 实施一个并发操作
     * @param work 工作者
     * @param result 校验者
     */
    private fun concurrentLogic(work: (String) -> Unit, result: ((excepted: String) -> Unit)) {
        val count = Random(System.currentTimeMillis()).nextInt(10, 20)
        val executor = Executors.newFixedThreadPool(count)
        try {
            val semaphore = Semaphore(0)
            val word = "h"
            for (i in 1..count)
                executor.submit {
                    try {
                        work(word)
                    } finally {
                        semaphore.release()
                    }
                }

            semaphore.acquire(count)


            result(word.repeat(count))
        } finally {
            executor.shutdown()
        }
    }

}