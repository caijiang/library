package me.jiangcai.common.test

import com.fasterxml.jackson.databind.JsonNode
import me.jiangcai.common.test.bug.ParamFilter
import org.apache.commons.logging.LogFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.mock.web.MockHttpSession
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.FilterChainProxy
import org.springframework.security.web.context.HttpRequestResponseHolder
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup
import org.springframework.test.web.servlet.setup.MockMvcConfigurer
import org.springframework.web.context.WebApplicationContext
import java.io.IOException
import java.io.InputStream
import java.net.URI
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author CJ
 */
@Suppress("MemberVisibilityCanBePrivate", "unused", "SpringKotlinAutowiredMembers")
@RunWith(SpringJUnit4ClassRunner::class)
@WebAppConfiguration
@ActiveProfiles("test")
abstract class MvcTest : CommonTest() {

    private val log = LogFactory.getLog(MvcTest::class.java)

    private val httpSessionSecurityContextRepository = HttpSessionSecurityContextRepository()
    /**
     * 自动注入应用程序上下文
     */
    @Autowired(required = false)
    private var context: WebApplicationContext? = null
    @Autowired(required = false)
    private var springSecurityFilter: FilterChainProxy? = null
    @Autowired(required = false)
    private var mockMvcConfigurer: MockMvcConfigurer? = null

    @Suppress("MemberVisibilityCanBePrivate")
    protected lateinit var mockMvc: MockMvc

    /**
     * 准备测试环境所需的各个字段
     */
    @Before
    fun prepareFields() {

        createMockMVC()

//        if (mockMvc == null)
//            return

        // 现在创建其他
//        createWebClient()
//
//        createWebDriver()
    }

    /**
     * 创建[.mockMvc]
     * <span>2.2以后需要添加更多的filter或者其他什么的可以覆盖[.buildMockMVC]不用再重新实现一次这个
     * 方法了。</span>
     */
    private fun createMockMVC() {
        MockitoAnnotations.initMocks(this)
        // ignore it, so it works in no-web fine.
        if (context == null) {

            log.error("working no-web environment.")
            return
        }

        var builder: DefaultMockMvcBuilder = webAppContextSetup(context).addFilters(ParamFilter())

        if (springSecurityFilter != null) {
            builder = builder.addFilters(object : Filter {
                @Throws(ServletException::class)
                override fun init(filterConfig: FilterConfig) {

                }

                @Throws(IOException::class, ServletException::class)
                override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
                    val authentication = autoAuthentication()

                    if (authentication != null) {
                        val holder =
                            HttpRequestResponseHolder(request as HttpServletRequest, response as HttpServletResponse)
                        val context = httpSessionSecurityContextRepository.loadContext(holder)

                        context.authentication = authentication

                        SecurityContextHolder.getContext().authentication = authentication

                        httpSessionSecurityContextRepository.saveContext(
                            context,
                            holder.request,
                            holder.response
                        )
                    }
                    chain.doFilter(request, response)
                }

                override fun destroy() {

                }
            })
        }

        builder = buildMockMVC(builder)
        if (springSecurityFilter != null) {
            builder = builder.addFilters(springSecurityFilter)
        }

        if (mockMvcConfigurer != null) {
            builder = builder.apply(mockMvcConfigurer)
        }
        mockMvc = builder.build()
    }

    /**
     * 如果没有激活Spring安全框架 则该方法无效
     *
     * @return 所用MVC请求都将使用该身份；如果为null则不会执行
     */
    protected fun autoAuthentication(): Authentication? {
        return null
    }

    /**
     * 构建[.mockMvc]的辅助方法
     *
     * @param builder builder
     * @return new Builder
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected fun buildMockMVC(builder: DefaultMockMvcBuilder): DefaultMockMvcBuilder {
        return builder
    }

//    #############断言

    /**
     * 如果遇见302一直执行get
     *
     * @param perform 操作
     * @param session session
     * @return 操作
     * @throws Exception
     */
    @Throws(Exception::class)
    protected fun redirectTo(perform: ResultActions, session: MockHttpSession?): ResultActions {
        val response = perform.andReturn().response
        if (response.status == 302) {
            val uri = response.redirectedUrl
            return if (session == null) redirectTo(mockMvc.perform(get(uri)), null) else redirectTo(
                mockMvc.perform(
                    get(uri).session(
                        session
                    )
                ), session
            )
        }
        return perform
    }

    /**
     * 断言输入json是一个数组,并且结构上跟inputStream类似
     *
     * @param json
     * @param inputStream
     * @throws IOException
     */
    @Throws(IOException::class)
    protected fun assertSimilarJsonArray(json: JsonNode, inputStream: InputStream) {
        assertThat(json.isArray)
            .isTrue()
        val mockArray = objectMapper.readTree(inputStream)
        val mockOne = mockArray.get(0)

        assertSimilarJsonObject(json.get(0), mockOne)
    }

    /**
     * 断言实际json是类似期望json的
     *
     * @param actual
     * @param excepted
     */
    protected fun assertSimilarJsonObject(actual: JsonNode, excepted: JsonNode) {
        assertThat(actual.isObject)
            .isTrue()
        assertThat(actual.fieldNames())
            .containsAll(Iterable { excepted.fieldNames() })
    }

    /**
     * @param resource 参考资源
     * @return 应该是一个JSON Array资源
     */
    protected fun similarJsonArrayAs(resource: String): ResultMatcher {
        return ResultMatcher { result ->
            val resource1 = context!!.getResource(resource)
            val actual = objectMapper.readTree(result.response.contentAsByteArray)
            assertThat(actual.isArray)
                .isTrue()

            assertSimilarJsonArray(actual, resource1.inputStream)
        }
    }


    /**
     * @param resource 参考资源
     * @return 跟resource数据相对应的JSON Object
     */
    protected fun similarJsonObjectAs(resource: String): ResultMatcher {
        return ResultMatcher { result ->
            val resource1 = context!!.getResource(resource)
            val actual = objectMapper.readTree(result.response.contentAsByteArray)
            assertThat(actual.isObject)
                .isTrue()

            assertSimilarJsonObject(actual, objectMapper.readTree(resource1.inputStream))
        }
    }

    /**
     * @param resource Spring资源path
     * @return 结果跟资源的json格式相近
     */
    protected fun similarBootstrapDataTable(resource: String): ResultMatcher {
        return ResultMatcher { result ->
            val resource1 = context!!.getResource(resource)
            resource1.inputStream.use { inputStream ->
                val actual = objectMapper.readTree(result.response.contentAsByteArray)
                assertThat(actual.get("total").isNumber)
                    .isTrue()
                val rows = actual.get("rows")
                assertThat(rows.isArray)
                    .isTrue()
                if (rows.size() == 0) {
                    log.warn("响应的rows为空,无法校验")
                } else {
                    val exceptedAll = objectMapper.readTree(inputStream)
                    val excepted = exceptedAll.get("rows").get(0)

                    assertSimilarJsonObject(rows.get(0), excepted)
                }

            }
        }
    }

    /**
     * @param resource Spring资源path
     * @return 结果跟资源的json格式相近
     */
    protected fun similarJQueryDataTable(resource: String): ResultMatcher {
        return ResultMatcher { result ->
            val resource1 = context!!.getResource(resource)
            resource1.inputStream.use { inputStream ->
                val actual = objectMapper.readTree(result.response.contentAsByteArray)
                assertThat(actual.get("recordsTotal").isNumber)
                    .isTrue()
                assertThat(actual.get("recordsFiltered").isNumber)
                    .isTrue()
                assertThat(actual.get("draw").isNumber)
                    .isTrue()

                val rows = actual.get("data")
                assertThat(rows.isArray)
                    .isTrue()
                if (rows.size() == 0) {
                    log.warn("响应的rows为空,无法校验")
                } else {
                    val exceptedAll = objectMapper.readTree(inputStream)
                    val excepted = exceptedAll.get("data").get(0)

                    assertSimilarJsonObject(rows.get(0), excepted)
                }

            }
        }
    }

    /**
     * Create a [MockHttpServletRequestBuilder] for a GET request.
     *
     * @param urlTemplate  a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     */
    protected operator fun get(urlTemplate: String, vararg urlVariables: Any?): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.get(urlTemplate, *urlVariables)
    }

    /**
     * Create a [MockHttpServletRequestBuilder] for a GET request.
     *
     * @param uri the URL
     */
    protected operator fun get(uri: URI): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.get(uri)
    }

    /**
     * Create a [MockHttpServletRequestBuilder] for a POST request.
     *
     * @param urlTemplate  a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     */
    protected fun post(urlTemplate: String, vararg urlVariables: Any?): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.post(urlTemplate, *urlVariables)
    }

    /**
     * Create a [MockHttpServletRequestBuilder] for a POST request.
     *
     * @param uri the URL
     */
    protected fun post(uri: URI): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.post(uri)
    }

    /**
     * Create a [MockHttpServletRequestBuilder] for a PUT request.
     *
     * @param urlTemplate  a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     */
    protected fun put(urlTemplate: String, vararg urlVariables: Any?): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.put(urlTemplate, *urlVariables)
    }

    /**
     * Create a [MockHttpServletRequestBuilder] for a PUT request.
     *
     * @param uri the URL
     */
    protected fun put(uri: URI): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.put(uri)
    }

    /**
     * Create a [MockHttpServletRequestBuilder] for a PATCH request.
     *
     * @param urlTemplate  a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     */
    protected fun patch(urlTemplate: String, vararg urlVariables: Any?): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.patch(urlTemplate, *urlVariables)
    }

    /**
     * Create a [MockHttpServletRequestBuilder] for a PATCH request.
     *
     * @param uri the URL
     */
    protected fun patch(uri: URI): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.patch(uri)
    }

    /**
     * Create a [MockHttpServletRequestBuilder] for a DELETE request.
     *
     * @param urlTemplate  a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     */
    protected fun delete(urlTemplate: String, vararg urlVariables: Any?): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.delete(urlTemplate, *urlVariables)
    }

    /**
     * Create a [MockHttpServletRequestBuilder] for a DELETE request.
     *
     * @param uri the URL
     */
    protected fun delete(uri: URI): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.delete(uri)
    }

    /**
     * Create a [MockHttpServletRequestBuilder] for an OPTIONS request.
     *
     * @param urlTemplate  a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     */
    protected fun options(urlTemplate: String, vararg urlVariables: Any?): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.options(urlTemplate, *urlVariables)
    }

    /**
     * Create a [MockHttpServletRequestBuilder] for an OPTIONS request.
     *
     * @param uri the URL
     */
    protected fun options(uri: URI): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.options(uri)
    }

    /**
     * Create a [MockHttpServletRequestBuilder] for a HEAD request.
     *
     * @param urlTemplate  a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     */
    protected fun head(urlTemplate: String, vararg urlVariables: Any?): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.head(urlTemplate, *urlVariables)
    }

    /**
     * Create a [MockHttpServletRequestBuilder] for a HEAD request.
     *
     * @param uri the URL
     */
    protected fun head(uri: URI): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.head(uri)
    }

    /**
     * Create a [MockHttpServletRequestBuilder] for a request with the given HTTP method.
     *
     * @param httpMethod   the HTTP method
     * @param urlTemplate  a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     */
    protected fun request(
        httpMethod: HttpMethod,
        urlTemplate: String,
        vararg urlVariables: Any?
    ): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.request(httpMethod, urlTemplate, *urlVariables)
    }

    /**
     * Create a [MockHttpServletRequestBuilder] for a request with the given HTTP method.
     *
     * @param httpMethod the HTTP method (GET, POST, etc)
     * @param uri        the URL
     */
    protected fun request(httpMethod: HttpMethod, uri: URI): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.request(httpMethod, uri)
    }

    /**
     * Create a [MockMultipartHttpServletRequestBuilder] for a multipart request.
     *
     * @param urlTemplate  a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     */
    protected fun fileUpload(urlTemplate: String, vararg urlVariables: Any?): MockMultipartHttpServletRequestBuilder {
        return MockMvcRequestBuilders.fileUpload(urlTemplate, *urlVariables)
    }

    /**
     * Create a [MockMultipartHttpServletRequestBuilder] for a multipart request.
     *
     * @param uri the URL
     */
    protected fun fileUpload(uri: URI): MockMultipartHttpServletRequestBuilder {
        return MockMvcRequestBuilders.fileUpload(uri)
    }

    /**
     * Print [MvcResult] details to the "standard" output stream.
     *
     * @see System.out
     */
    protected fun print(): ResultHandler {
        return MockMvcResultHandlers.print()
    }
}