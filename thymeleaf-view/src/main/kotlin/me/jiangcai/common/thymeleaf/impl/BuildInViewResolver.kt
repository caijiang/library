package me.jiangcai.common.thymeleaf.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.core.env.Environment
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.servlet.View
import org.thymeleaf.spring5.SpringTemplateEngine
import org.thymeleaf.spring5.messageresolver.SpringMessageResolver
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver
import org.thymeleaf.spring5.view.ThymeleafViewResolver
import java.util.*
import javax.annotation.PostConstruct

/**
 * @author CJ
 */
@Component
class BuildInViewResolver(
    @Autowired
    private val environment: Environment
) : ThymeleafViewResolver() {

    override fun resolveViewName(viewName: String?, locale: Locale?): View? {
        if (viewName?.startsWith("thymeleaf:") == true) {
            return super.resolveViewName(viewName.removePrefix("thymeleaf:"), locale)
        }
        return null
    }

    @Suppress("SpringKotlinAutowiredMembers")
    @Autowired(required = false)
    private val sysMessageSource: MessageSource? = null

    @PostConstruct
    fun init() {
        val templateResolver = SpringResourceTemplateResolver()
        templateResolver.setApplicationContext(applicationContext)
        templateResolver.name = "BuildInSpringResourceTemplateResolver"
        templateResolver.order = 0
        // 具有极高的优先级,所以它在获取解决方案时  应该判定是否存在该资源
        templateResolver.prefix = ""
        templateResolver.suffix = ".html"
        templateResolver.characterEncoding = "UTF-8"
        if (environment.acceptsProfiles("dev")) {
            templateResolver.isCacheable = false
        }

        val engine = SpringTemplateEngine()
        engine.setTemplateResolver(templateResolver)
        if (sysMessageSource != null) {
            engine.setMessageSource(sysMessageSource)
            val messageResolver = SpringMessageResolver()
            messageResolver.messageSource = sysMessageSource
            engine.setMessageResolver(messageResolver)
        }

        order = 1
        contentType = MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"
        characterEncoding = "UTF-8"
        templateEngine = engine
    }

}