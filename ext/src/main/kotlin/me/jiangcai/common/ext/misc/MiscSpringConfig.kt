package me.jiangcai.common.ext.misc

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

/**
 * 需要手动使用的几个功能。 导入方式:
 * ```kotlin
 * @Import(MiscSpringConfig::class)
 * ```
 * 功能有:
 * ## GET /echo
 * ## GET /echo/???
 * ## GET /developmentHelpStatus
 * 如果有响应表示在开发或者演示平台。
 * ## POST /erase 清理所有数据并且重启整个应用。
 * ### 响应
 * - seconds(可通过common.seconds.toRestart定制)
 * - message
 * ## POST /passwordChanger
 * 修改当前登录用户密码。
 * ### 请求
 * - originalPassword: 原密码
 * - password: 新密码
 * @author CJ
 */
//@Suppress("SpringFacetCodeInspection")
@Configuration
@ComponentScan("me.jiangcai.common.ext.misc")
open class MiscSpringConfig