package com.mingshz.owner

import me.jiangcai.common.test.MvcTest
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder

/**
 * @author CJ
 */
abstract class OwnerTest : MvcTest() {
    override fun buildMockMVC(builder: DefaultMockMvcBuilder): DefaultMockMvcBuilder {
        return builder.addFilters(OwnerFilter())
    }
}