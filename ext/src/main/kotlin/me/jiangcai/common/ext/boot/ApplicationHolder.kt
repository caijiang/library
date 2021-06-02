package me.jiangcai.common.ext.boot

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.SpringApplication
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext

/**
 * 可以管理一个 spring boot 的实例，比如实施重启等操作。
 * 用法如下：
 * ```kotlin
 * fun main(args: Array<String>) {
 *    ApplicationHolder.start<Application>(args)
 * }
 * ```
 * @author CJ
 */
@Suppress("unused")
class ApplicationHolder {
    companion object {
        var context: ConfigurableApplicationContext? = null
        var lastAppClass: Class<*>? = null
        inline fun <reified T : Any> start(args: Array<String>) {
            context = runApplication<T>(*args)
            lastAppClass = T::class.java
        }

        /**
         * 重新启动
         */
        fun restart() {
            if (context == null || lastAppClass == null)
                throw RuntimeException("必须使用， ApplicationHolder.start() 方式启动应用。")
            val args = context!!.getBean(ApplicationArguments::class.java)
            val thread = Thread {
                context?.close()
                context = SpringApplication.run(lastAppClass, *args.sourceArgs)
            }
            thread.isDaemon = false
            thread.start()
        }


    }
}