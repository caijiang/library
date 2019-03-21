package me.jiangcai.common.ext.thread

import org.junit.Test


/**
 * @author CJ
 */
class ThreadSafeCheckerTest {

    @Test
    fun forProject() {
        ThreadSafeChecker().forProject("demo")
    }
}