package me.jiangcai.common.bs

/**
 * @author CJ
 */
interface BusinessLocker {

    /**
     * @return 业务锁
     */
    fun toLocker(): Any
}