package me.jiangcai.common.test

import org.junit.Test
import org.springframework.test.context.web.WebAppConfiguration

/**
 * @author CJ
 */
@WebAppConfiguration
class MvcTestTest : MvcTest() {


    @Test
    fun randomTest() {
        var x = 50
        while (x-- > 0) {
            System.out.println(randomMobile())
            System.out.println(randomEmailAddress())
            System.out.println(randomHttpURL())
        }
    }
}