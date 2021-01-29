package me.jiangcai.common.resource

import me.jiangcai.common.resource.bean.LocalResourceService
import me.jiangcai.common.resource.bean.OSSResourceService
import me.jiangcai.common.resource.bean.VFSResourceService
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
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
 * * jiangcai.resource.host 临时访问主机名称，只在 development or test 环境中生效
 *
 * ## 上传服务
 * 同时会增加资源上传服务，默认路径 /uploadTempResource 数据字段 data; 如果启用了 Spring Security 则要求 ROLE_UPLOAD_TEMP 权限
 *
 * @author CJ
 */
@Suppress("SpringFacetCodeInspection")
@Configuration
@Import(DevelopmentConfig::class)
@ComponentScan("me.jiangcai.common.resource.bean")
open class ResourceSpringConfig(
    @Autowired
    private val environment: Environment
) {

    private val log = LogFactory.getLog(ResourceSpringConfig::class.java)

    @Autowired(required = false)
    private var webApplicationContext: WebApplicationContext? = null

    @Bean
    open fun resourceService(): ResourceService {
        if (environment.getProperty("com.aliyun.oss.endpoint") != null
            && environment.getProperty("com.aliyun.oss.bucketName") != null
        ) {
            return OSSResourceService(environment)
        }

        // 如果已提供了 关键变量
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "USELESS_CAST")
        val uri = environment.getProperty(
            "jiangcai.resource.http.uri",
            environment.getProperty("me.jiangcai.lib.resource.http.uri")
        ) as String?

        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "USELESS_CAST")
        val home = environment.getProperty(
            "jiangcai.resource.home",
            environment.getProperty("me.jiangcai.lib.resource.home")
        ) as String?

        if (uri != null && home != null)
            return VFSResourceService(uri, home)

        // 开发环境
        if (environment.acceptsProfiles(Profiles.of("development"))) {
            log.warn("ResourceService working in Development mode.")
            return LocalResourceService(environment, DevelopmentConfig.getResourcesHome())
        }
        // 测试环境
        if (environment.acceptsProfiles(Profiles.of(" test"))) {
            log.warn("ResourceService working in Test mode.")
            return LocalResourceService(environment, TestConfig.getResourcesHome())
        }

        throw IllegalArgumentException("ResourceService can not work without jiangcai.resource.http.uri,jiangcai.resource.home envs. (production mode)")
//        return VFSResourceService(environment, webApplicationContext)
    }

}