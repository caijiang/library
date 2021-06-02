package me.jiangcai.common.ext.misc

import me.jiangcai.common.ext.boot.ApplicationHolder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.lang.Nullable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody
import javax.sql.DataSource

/**
 * 演示后台应该允许
 * * 访问系统设置
 * * 抹掉所有数据
 * @author CJ
 */
@Suppress("SqlResolve", "SqlDialectInspection")
@Controller
@Profile("development", "staging")
open class DevelopmentHelpController(
    @Value("\${common.seconds.toRestart:60}")
    private val seconds: Int,
    @Nullable
    private val dataSource: DataSource?
) {

    @GetMapping("/developmentHelpStatus")
    @ResponseBody
    fun touch() = true

    // seconds 重启完成的秒数 0 表示没有成功
    // message 信息 直接展示即可
    @PostMapping("/erase")
    @PreAuthorize("hasAnyRole('ROOT')")
    @ResponseBody
    fun erase(): Map<String, Any> {
        if (dataSource == null)
            return mapOf(
                "seconds" to 0,
                "message" to "没有数据源"
            )
        dataSource.connection.use { connection ->
            val name = connection.catalog ?: connection.schema
            connection.createStatement().use {
                it.execute("drop database `${name}`")
                it.execute("create database `${name}`")
            }
        }
        Thread {
            Thread.sleep(100)
            ApplicationHolder.restart()
        }
            .apply {
                isDaemon = false
            }
            .start()
        return mapOf(
            "seconds" to seconds,
            "message" to "数据库已清空，系统正在自动重启中……"
        )
    }

}