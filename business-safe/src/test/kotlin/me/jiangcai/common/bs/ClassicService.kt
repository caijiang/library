package me.jiangcai.common.bs

import org.springframework.transaction.annotation.Transactional

/**
 * @author CJ
 */
interface ClassicService {

    //    @PostConstruct
    @Transactional
    fun init()

    /**
     * 很简单
     * @return memory+name
     */
    fun contactWithMemory(name: String): String

    /**
     * 很简单
     * @return memory+name
     */
    @BusinessSafe
    fun contactWithMemoryInLock(name: String): String

    @Transactional
    fun addNameToSimple(name: String): String

    @Transactional
    @BusinessSafe
    fun addNameToSimpleInLock(name: String): String
}