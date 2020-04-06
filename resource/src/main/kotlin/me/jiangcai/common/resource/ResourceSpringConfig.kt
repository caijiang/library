package me.jiangcai.common.resource

import me.jiangcai.common.resource.bean.OSSResourceService
import me.jiangcai.common.resource.bean.VFSResourceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.env.Environment
import org.springframework.web.context.WebApplicationContext

/**
 * 可以载入资源管理服务的Spring配置
 *
 * ## 支持以下配置方式
 * ### OSS
 * * com.aliyun.oss.bucketName bucket 名称 比如 test
 * * com.aliyun.oss.urlPrefix OSS资源 访问前缀比如 https://test.oss-cn-hangzhou.aliyuncs.com/
 * * com.aliyun.oss.endpoint
 * * com.aliyun.accessKeyId
 * * com.aliyun.secretAccessKey
 * ### 本地或者 VFS
 * * jiangcai.resource.http.uri 资源http可访问地址 比如 http://www.hello.com/resources
 * * jiangcai.resource.home 资源的实际保存位置 比如 /var/rs 也支持远程服务器 sftp://user:host/home/user
 * * jiangcai.resource.host 临时访问主机名称，只在 development 环境中生效
 *
 * @author CJ
 */
@Configuration
@Import(DevelopmentConfig::class)
@ComponentScan("me.jiangcai.common.resource.bean")
open class ResourceSpringConfig(
    @Autowired
    private val environment: Environment
) {

    @Autowired(required = false)
    private var webApplicationContext: WebApplicationContext? = null

    @Bean
    open fun resourceService(): ResourceService {
        if (environment.getProperty("com.aliyun.oss.endpoint") != null
            && environment.getProperty("com.aliyun.oss.bucketName") != null
        ) {
            return OSSResourceService(environment)
        }

        return VFSResourceService(environment, webApplicationContext)
    }

}