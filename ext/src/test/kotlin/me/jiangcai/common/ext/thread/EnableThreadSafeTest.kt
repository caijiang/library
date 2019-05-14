package me.jiangcai.common.ext.thread

import me.jiangcai.common.ext.thread.job.Config
import me.jiangcai.common.test.MvcTest
import org.junit.Test
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

/**
 * @author CJ
 */
@ActiveProfiles("test_thread_safe")
@ContextConfiguration(classes = [Config::class])
class EnableThreadSafeTest : MvcTest() {

    @Test
    fun go() {
        mockMvc.perform(
            get("/hello")
        )
            .andDo(print())
    }

}