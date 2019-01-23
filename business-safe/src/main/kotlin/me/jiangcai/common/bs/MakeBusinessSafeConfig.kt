package me.jiangcai.common.bs

import org.apache.commons.logging.LogFactory
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.core.Ordered

/**
 * 需要在spring中引入该配置
 * @author CJ
 */
@Aspect
@Configuration
@EnableAspectJAutoProxy
open class MakeBusinessSafeConfig : Ordered {

    override fun getOrder(): Int {
        return Ordered.HIGHEST_PRECEDENCE
    }

    private val log = LogFactory.getLog(MakeBusinessSafeConfig::class.java)

    @Suppress("unused")
    @Pointcut("@annotation(me.jiangcai.common.bs.BusinessSafe)")
    fun safePoint() {
    }

    @Around("safePoint()")
    @Throws(Throwable::class)
    fun aroundSave(pjp: ProceedingJoinPoint): Any {
        // start stopwatch

        val lock = toLocker(pjp)
        if (log.isDebugEnabled)
            log.debug("prepare into business-safe method:" + pjp.toShortString() + " with lockers:" + lock.asList())
        return multiLock(lock, pjp)
    }

    private fun toLocker(pjp: ProceedingJoinPoint): Array<Any> {
        val args = pjp.args
        val x = args.find {
            it is MultipleBusinessLocker
        } as MultipleBusinessLocker?
        if (x != null)
            return x.toLockers()
        val y = args.find { it is BusinessLocker } as BusinessLocker?

        if (y != null)
            return arrayOf(y.toLocker())

        if (args[0] is Number) {
            // 数字无法成为锁，所以我们……使用字符串代替
            return arrayOf("${pjp.toShortString()}#${args[0]}".intern())
        }

        return arrayOf(args[0])
    }


    private fun multiLock(locks: Array<Any>, pjp: ProceedingJoinPoint): Any {
        val lock = locks[0]
        val lockerObject = when (lock) {
            is String ->
                lock.intern()
            else ->
                lock
        }
        if (locks.size == 1) {
            synchronized(lockerObject) {
                try {
                    log.debug(Thread.currentThread().name + " entering business-safe method:" + pjp.toShortString() + " with locker instance: " + lock)
                    return pjp.proceed()
                } finally {
                    log.debug(Thread.currentThread().name + " exited business-safe method:" + pjp.toShortString())
                }
            }
        } else {
            val newLocks = locks.copyOfRange(1, locks.size)
            synchronized(lockerObject) {
                log.debug(Thread.currentThread().name + " lock:" + lock)
                try {
                    return multiLock(newLocks, pjp)
                } finally {
                    log.debug(Thread.currentThread().name + "unlock:" + lock)
                }
            }

        }
    }
}