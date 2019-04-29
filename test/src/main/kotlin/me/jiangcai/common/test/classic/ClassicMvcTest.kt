package me.jiangcai.common.test.classic

import com.mingshz.login.ClassicLoginService
import com.mingshz.login.entity.Login
import me.jiangcai.common.test.MvcTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.context.HttpRequestResponseHolder
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import java.net.URI
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author CJ
 */
@Suppress("SpringJavaAutowiredMembersInspection")
abstract class ClassicMvcTest : MvcTest() {

    companion object {
        const val LOGIN_ID_HEAD = "X_l_id_x"
    }

    private val httpSessionSecurityContextRepository = HttpSessionSecurityContextRepository()

    override fun buildMockMVC(builder: DefaultMockMvcBuilder): DefaultMockMvcBuilder {
        return builder
            .addFilters(object : Filter {
                override fun destroy() {
                }

                override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
                    if (request is HttpServletRequest && request.getHeader(LOGIN_ID_HEAD) != null) {
                        val login = classicLoginService.findLogin(request.getHeader(LOGIN_ID_HEAD).toLong())
                        try {
                            val holder =
                                HttpRequestResponseHolder(request, response as HttpServletResponse)
                            val context = httpSessionSecurityContextRepository.loadContext(holder)
                            context.authentication = LoginAuthentication(login)
                            SecurityContextHolder.setContext(context)
                            httpSessionSecurityContextRepository.saveContext(context, holder.request, holder.response)
                            chain?.doFilter(request, response)

                        } finally {
                            SecurityContextHolder.clearContext()
                        }
                    } else
                        chain?.doFilter(request, response)
                }

                override fun init(filterConfig: FilterConfig?) {
                }

            })
    }

    @Autowired
    private lateinit var classicLoginService: ClassicLoginService<Login>

    protected fun get(urlTemplate: String, login: Login, vararg urlVariables: Any?): MockHttpServletRequestBuilder {
        return get(urlTemplate, *urlVariables)
            .header(LOGIN_ID_HEAD, login.id)
    }

    protected fun get(uri: URI, login: Login): MockHttpServletRequestBuilder {
        return get(uri)
            .header(LOGIN_ID_HEAD, login.id)
    }

    protected fun post(urlTemplate: String, login: Login, vararg urlVariables: Any?): MockHttpServletRequestBuilder {
        return post(urlTemplate, *urlVariables)
            .header(LOGIN_ID_HEAD, login.id)
    }

    protected fun post(uri: URI, login: Login): MockHttpServletRequestBuilder {
        return post(uri)
            .header(LOGIN_ID_HEAD, login.id)
    }

    protected fun put(urlTemplate: String, login: Login, vararg urlVariables: Any?): MockHttpServletRequestBuilder {
        return put(urlTemplate, *urlVariables)
            .header(LOGIN_ID_HEAD, login.id)
    }

    protected fun put(uri: URI, login: Login): MockHttpServletRequestBuilder {
        return put(uri)
            .header(LOGIN_ID_HEAD, login.id)
    }

    protected fun patch(urlTemplate: String, login: Login, vararg urlVariables: Any?): MockHttpServletRequestBuilder {
        return patch(urlTemplate, *urlVariables)
            .header(LOGIN_ID_HEAD, login.id)
    }

    protected fun patch(uri: URI, login: Login): MockHttpServletRequestBuilder {
        return patch(uri)
            .header(LOGIN_ID_HEAD, login.id)
    }

    protected fun delete(urlTemplate: String, login: Login, vararg urlVariables: Any?): MockHttpServletRequestBuilder {
        return delete(urlTemplate, *urlVariables)
            .header(LOGIN_ID_HEAD, login.id)
    }

    protected fun delete(uri: URI, login: Login): MockHttpServletRequestBuilder {
        return delete(uri)
            .header(LOGIN_ID_HEAD, login.id)
    }

    protected fun options(urlTemplate: String, login: Login, vararg urlVariables: Any?): MockHttpServletRequestBuilder {
        return options(urlTemplate, *urlVariables)
            .header(LOGIN_ID_HEAD, login.id)
    }

    protected fun options(uri: URI, login: Login): MockHttpServletRequestBuilder {
        return options(uri)
            .header(LOGIN_ID_HEAD, login.id)
    }

    protected fun head(urlTemplate: String, login: Login, vararg urlVariables: Any?): MockHttpServletRequestBuilder {
        return head(urlTemplate, *urlVariables)
            .header(LOGIN_ID_HEAD, login.id)
    }

    protected fun head(uri: URI, login: Login): MockHttpServletRequestBuilder {
        return head(uri)
            .header(LOGIN_ID_HEAD, login.id)
    }

    protected fun request(
        httpMethod: HttpMethod,
        urlTemplate: String,
        login: Login,
        vararg urlVariables: Any?
    ): MockHttpServletRequestBuilder {
        return request(httpMethod, urlTemplate, *urlVariables)
            .header(LOGIN_ID_HEAD, login.id)
    }

    protected fun request(httpMethod: HttpMethod, uri: URI, login: Login): MockHttpServletRequestBuilder {
        return request(httpMethod, uri)
            .header(LOGIN_ID_HEAD, login.id)
    }

    protected fun fileUpload(
        urlTemplate: String,
        login: Login,
        vararg urlVariables: Any?
    ): MockMultipartHttpServletRequestBuilder {
        return fileUpload(urlTemplate, *urlVariables)
            .header(LOGIN_ID_HEAD, login.id) as MockMultipartHttpServletRequestBuilder
    }

    protected fun fileUpload(uri: URI, login: Login): MockMultipartHttpServletRequestBuilder {
        return fileUpload(uri)
            .header(LOGIN_ID_HEAD, login.id) as MockMultipartHttpServletRequestBuilder
    }
}