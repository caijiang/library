package me.jiangcai.common.test

import com.fasterxml.jackson.databind.ObjectMapper
import me.jiangcai.common.ext.nextEmailAddress
import me.jiangcai.common.ext.nextHttpURL
import me.jiangcai.common.ext.nextMobileOfChina
import org.springframework.test.context.ActiveProfiles
import kotlin.random.Random

/**
 * 通用测试基类
 * @author CJ
 */
@ActiveProfiles("test")
abstract class CommonTest {

    @Suppress("MemberVisibilityCanBePrivate")
    protected val random = Random(System.currentTimeMillis())
    protected val objectMapper = ObjectMapper()

    /**
     * @return 获取随机http url
     */
    protected fun randomHttpURL(): String {
        return random.nextHttpURL()
    }

    /**
     * @return 获取随机email地址
     */
    protected fun randomEmailAddress(): String {
        return random.nextEmailAddress()
    }

    protected fun randomMobile(): String {
        return random.nextMobileOfChina()
    }

}