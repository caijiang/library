package com.mingshz.owner

import com.mingshz.owner.single.Config
import com.mingshz.owner.single.SingleOwner
import org.junit.Test
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * 测试 [EnableSingleOwner]
 * @author CJ
 */
@ContextConfiguration(classes = [Config::class])
class EnableSingleOwnerTest : OwnerTest() {

    @Test
    fun go() {
        mockMvc.perform(get("/echoOwner"))
            .andExpect(status().isOk)
            .andExpect(content().string(SingleOwner().name))
    }
}