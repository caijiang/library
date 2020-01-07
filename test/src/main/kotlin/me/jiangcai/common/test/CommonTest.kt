package me.jiangcai.common.test

import com.fasterxml.jackson.databind.ObjectMapper
import me.jiangcai.common.ext.nextDomain
import me.jiangcai.common.ext.nextEmailAddress
import me.jiangcai.common.ext.nextHttpURL
import me.jiangcai.common.ext.nextMobileOfChina
import org.springframework.test.context.ActiveProfiles
import java.util.*
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

    protected fun nextDomain(): String {
        return random.nextDomain()
    }

    protected fun randomMobile(): String {
        return random.nextMobileOfChina()
    }

    /**
     * @return 尽可能唯一的随机字符串
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected fun randomString(): String {
        return UUID.randomUUID().toString()
    }

    /**
     * @param maxLength 最大长度
     * @return 尽可能唯一的随机字符串
     */
    protected fun randomString(maxLength: Int): String {
        val stringBuilder = StringBuilder()
        while (true) {
            if (stringBuilder.length > maxLength) {
                stringBuilder.setLength(maxLength)
                break
            }
            stringBuilder.append(randomString())
        }
        return stringBuilder.toString()
    }

}