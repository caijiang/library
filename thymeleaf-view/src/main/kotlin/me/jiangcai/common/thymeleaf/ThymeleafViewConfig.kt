package me.jiangcai.common.thymeleaf

import me.jiangcai.common.thymeleaf.impl.BuildInViewResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

/**
 * 在载入该处理器之后，所有形似 thymeleaf:XXX 的视图都会被thymeleaf处理，其中XXX是视图模板的资源名字
 * @author CJ
 */
@Configuration
@ComponentScan("me.jiangcai.common.thymeleaf.impl")
open class ThymeleafViewConfig : WebMvcConfigurerAdapter() {

    @Autowired
    private lateinit var buildInViewResolver: BuildInViewResolver

    override fun configureViewResolvers(registry: ViewResolverRegistry) {
        super.configureViewResolvers(registry)
        registry.viewResolver(buildInViewResolver)
    }
}