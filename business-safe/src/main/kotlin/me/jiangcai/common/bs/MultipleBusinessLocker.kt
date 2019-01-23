package me.jiangcai.common.bs

/**
 * @author CJ
 */
interface MultipleBusinessLocker {
    /**
     * @return 业务锁
     */
    fun toLockers(): Array<Any>
}