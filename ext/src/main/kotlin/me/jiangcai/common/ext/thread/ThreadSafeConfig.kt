package me.jiangcai.common.ext.thread

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.ui.ModelMap
import org.springframework.web.context.request.WebRequest
import org.springframework.web.context.request.WebRequestInterceptor
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

/**
 * @author CJ
 */
@Configuration
@EnableWebMvc
internal open class ThreadSafeConfig(
    @Autowired
    private val environment: Environment
) : WebMvcConfigurerAdapter() {

    companion object {
        internal var name: String? = null
    }

    override fun addInterceptors(registry: InterceptorRegistry?) {
        if (environment.acceptsProfiles("test_thread_safe") || !environment.acceptsProfiles("test"))
            registry?.addWebRequestInterceptor(
                object : WebRequestInterceptor {
                    override fun preHandle(request: WebRequest?) {
                        name?.let {
                            ThreadSafeChecker().forProject(it)
                        }
                    }

                    override fun postHandle(request: WebRequest?, model: ModelMap?) {
                    }

                    override fun afterCompletion(request: WebRequest?, ex: java.lang.Exception?) {
                    }

                }
            )
    }


}