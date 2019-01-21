package me.jiangcai.common.test.bug

import org.springframework.http.InvalidMediaTypeException
import org.springframework.http.MediaType
import org.springframework.util.StreamUtils
import java.io.IOException
import java.net.URLDecoder
import java.nio.charset.Charset
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

/**
 * @author CJ
 */
class ParameterRequestWrapper(request: HttpServletRequest, private val params: MutableMap<String, Array<String>>) :
    HttpServletRequestWrapper(request) {
    init {
        renewParameterMap(request)
    }


    override fun getParameter(name: String): String? {
        val result: String?

        val v = params[name]
        result = if (v == null) {
            null
        } else {
            if (v.isNotEmpty()) {
                v[0]
            } else {
                null
            }
        }

        return result
    }

    override fun getParameterMap(): Map<String, Array<String>> {
        return params
    }

    override fun getParameterNames(): Enumeration<String> {
        return Vector<String>(params.keys).elements()
    }

    override fun getParameterValues(name: String): Array<String>? {
        return params[name]
    }

    /**
     * 处理post请求controller获取不到参数问题
     *
     * @param req 原生请求
     */
    @Throws(IOException::class)
    private fun renewParameterMap(req: HttpServletRequest) {
        // 只处理正确的content
        val mediaType: MediaType
        try {
            mediaType = MediaType.valueOf(req.contentType)
        } catch (ignored: InvalidMediaTypeException) {
            return
        }

        if (!MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(mediaType)) {
            return
        }

        val encoding = if (mediaType.charset != null) mediaType.charset.name() else "UTF-8"
        val queryString = StreamUtils.copyToString(req.inputStream, Charset.forName("UTF-8"))

        if (queryString.trim { it <= ' ' }.isNotEmpty()) {
            val params = queryString.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            for (param in params) {
                val splitIndex = param.indexOf("=")
                if (splitIndex == -1) {
                    continue
                }

                val originKey = param.substring(0, splitIndex)
                val key = URLDecoder.decode(originKey, encoding)
                if (splitIndex < param.length) {
                    val value = param.substring(splitIndex + 1)
                    val realValue = URLDecoder.decode(value, encoding)

//                    params.put
                    if ((this.params).putIfAbsent(key, arrayOf(realValue)) != null) {
                        (this.params).computeIfPresent(key) { _, strings ->
                            val na = arrayOfNulls<String>(strings.size + 1)
                            System.arraycopy(strings, 0, na, 0, strings.size)
                            na[na.size - 1] = realValue
                            @Suppress("UNCHECKED_CAST")
                            na as Array<String>
                        }
                    }
                }
            }
        }
    }
}