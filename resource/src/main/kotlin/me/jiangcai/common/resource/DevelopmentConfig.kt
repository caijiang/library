@file:Suppress("SpringFacetCodeInspection")

package me.jiangcai.common.resource

import me.jiangcai.common.resource.bean.VFSResourceService
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import java.io.File

/**
 * @author CJ
 */
@Configuration
@Profile("development")
open class DevelopmentConfig : WebMvcConfigurerAdapter() {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        super.addResourceHandlers(registry)
        registry.addResourceHandler(VFSResourceService.ServletContextResourcePath + "/**")
            .addResourceLocations("file:" + getResourcesHome())
    }

    companion object {
        fun getResourcesHome(): String {
            return System.getProperty("user.dir") + "/_resources/"
        }
    }
}

@Configuration
@Profile("test")
open class TestConfig : WebMvcConfigurerAdapter() {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        super.addResourceHandlers(registry)
        registry.addResourceHandler(VFSResourceService.ServletContextResourcePath + "/**")
            .addResourceLocations("file:" + getResourcesHome())
    }

    companion object {
        fun getResourcesHome(): String {
            return File(File("."), "_resources").absolutePath + "/"
        }
    }
}