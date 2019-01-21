package me.jiangcai.common.logging

import me.jiangcai.common.logging.impl.LoggingController
import me.jiangcai.common.thymeleaf.ThymeleafViewConfig
import org.apache.commons.logging.LogFactory
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.config.AppenderRef
import org.apache.logging.log4j.core.config.LoggerConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.EnumerablePropertySource
import java.util.*
import javax.annotation.PostConstruct
import javax.servlet.ServletContext

/**
 * 通用可载入的日志配置辅助。
 * 它可以自动实现自定义日志级别，目前还不支持增加日志写入目的。
 *
 * 原则上，只要存在任何log4j.开头的属性(包括上下文属性和系统属性)都将该值应用到相关日志级别。
 * 比如，设置了一个<code>log4j.org.luffy.lib</code>的属性，值为debug；则将生成新日志级别debug到org.luffy.lib
 *
 *
 * 同样也可以应用于无配置文件，它会采用<code>log4j.root.level</code>作为默认日志级别，该功能在1.9以后生效。
 *
 * **目前仅支持log4j2**
 *
 * @author CJ
 */
@Configuration
@Import(ThymeleafViewConfig::class)
@ComponentScan("me.jiangcai.common.logging.impl")
open class LoggingConfig(
    @Autowired
    private val loggingController: LoggingController,
    @Autowired
    private val environment: ConfigurableEnvironment
) : ApplicationListener<ContextRefreshedEvent> {
    private val log = LogFactory.getLog(LoggingConfig::class.java)

    companion object {
        const val ROOT_LEVEL = "root.level"

        /**
         * 可配置日志的权限
         */
        const val ROLE_MANAGER = "LOGGING_CONFIG"
    }


    @Autowired(required = false)
    private val servletContext: ServletContext? = null


    @PostConstruct
    fun init() {
        try {
            configLog4j(currentLoggingProperties())
        } catch (ignored: Throwable) {

        }

    }


    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        log.debug("Refresh Logging Config on start.")
        try {
            configLog4j(currentLoggingProperties())
        } catch (ignored: Throwable) {

        }

    }

    @EventListener(classes = [RefreshLoggingEvent::class])
    fun refreshLogging() {
        log.debug("Refresh Logging Config on event.")
        try {
            configLog4j(currentLoggingProperties())
        } catch (ignored: Throwable) {

        }

    }


    private fun configLog4j(properties: Properties) {
        val ctx = LogManager.getContext(false) as LoggerContext
        val config = ctx.configuration

        val appenderMap = config.appenders

        //如果没有配置 则默认输出级别更正为info 如果设置了 log4j.root.level 则依赖此项
        if (appenderMap.size == 1) {
            ctx.configuration.rootLogger.level = Level.toLevel(properties.getProperty(ROOT_LEVEL), Level.INFO)
        }

        if (appenderMap.isEmpty())
            return
        val refs = appenderMap.keys
            .map { name -> AppenderRef.createAppenderRef(name, null, null) }
            .toTypedArray()

        properties.stringPropertyNames().stream()
            .filter { name -> name != ROOT_LEVEL }
            .forEach { loggerName ->
                // remove first
                config.removeLogger(loggerName)

                val loggerConfig = LoggerConfig.createLogger(
                    false,
                    Level.toLevel(properties.getProperty(loggerName)),
                    loggerName,
                    "true",
                    refs,
                    null,
                    config,
                    null
                )

                config.addLogger(loggerName, loggerConfig)
                appenderMap.values.forEach { appender -> loggerConfig.addAppender(appender, null, null) }
            }

        ctx.updateLoggers()
    }

    /**
     *
     * 键为包名,值为键值
     *
     * 优先级别为servlet参数,spring环境,系统环境
     *
     * 必然包含root.level
     *
     * @return 获取当前日志配置
     */
    private fun currentLoggingProperties(): Properties {

        val properties = Properties()

        //        System.getProperties().stringPropertyNames()
        //                .stream()
        //                .filter(name -> name.startsWith("log4j."))
        //                .forEach(name -> {
        //                    String loggerName = name.substring(6);
        //                    properties.setProperty(loggerName, System.getProperty(name, "warn"));
        //                });

        environment.systemProperties.keys.stream().filter { name -> name.startsWith("log4j.") }
            .forEach { name ->
                val loggerName = name.substring(6)
                properties.setProperty(
                    loggerName,
                    environment.systemProperties.getOrDefault(name, "warn").toString()
                )
            }

        properties.setProperty(ROOT_LEVEL, environment.getProperty("log4j.root.level", "info"))

        environment.propertySources.forEach { propertySource ->
            //            if (propertySource.containsProperty("log4j.root.level")) {
            //                properties.setProperty(ROOT_LEVEL, propertySource.getProperty("log4j.root.level").toString());
            //            }
            if (propertySource is EnumerablePropertySource<*>) {
                val names = propertySource.propertyNames
                Arrays.stream(names).filter { name -> name.startsWith("log4j.") }
                    .forEach { name ->
                        val loggerName = name.substring(6)
                        properties.setProperty(loggerName, propertySource.getProperty(name).toString())
                    }
            }
        }

        if (servletContext != null) {
            val names = servletContext.initParameterNames
            while (names.hasMoreElements()) {
                val name = names.nextElement()
                if (name.startsWith("log4j.")) {
                    val loggerName = name.substring(6)
                    properties.setProperty(loggerName, servletContext.getInitParameter(name))
                }
            }
            //            String levelName = servletContext.getInitParameter("log4j.root.level");
            //            if (levelName != null) {
            //                properties.setProperty(ROOT_LEVEL, levelName);
            //            }
        }

        loggingController.getManageableConfigs().forEach {
            properties[it.key] = it.value
        }

        return properties
    }

}