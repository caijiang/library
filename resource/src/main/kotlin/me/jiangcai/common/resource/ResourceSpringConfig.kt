package me.jiangcai.common.resource

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

/**
 * 可以载入资源管理服务的Spring配置
 * * jiangcai.resource.http.uri 资源http可访问地址 比如 http://www.hello.com/resources
 * * jiangcai.resource.home 资源的实际保存位置 比如 /var/rs 也支持远程服务器 sftp://user:host/home/user
 *
 * @author CJ
 */
@Configuration
@ComponentScan("me.jiangcai.common.resource.bean")
open class ResourceSpringConfig