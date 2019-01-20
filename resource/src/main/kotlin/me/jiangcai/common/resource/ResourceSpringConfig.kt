package me.jiangcai.common.resource

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

/**
 * 可以载入资源管理服务的Spring配置
 * 使用VFS的应用可以参考{@link me.jiangcai.lib.resource.service.impl.VFSResourceService}
 *
 * @author CJ
 */
@Configuration
@ComponentScan("me.jiangcai.common.resource.bean")
open class ResourceSpringConfig