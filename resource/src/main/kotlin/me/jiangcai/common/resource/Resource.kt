package me.jiangcai.common.resource

import java.io.IOException
import java.net.URL

/**
 * 资源
 * 此处继承了Spring的Resource,添加更多资源行为
 * @author CJ
 */
interface Resource : org.springframework.core.io.Resource {

    /**
     * @return 以https或者http为schema的访问url
     * @throws IOException 产生时发生IO错误
     */
    @Throws(IOException::class)
    fun httpUrl(): URL

    /**
     * @return 这个资源在本系统中的path
     * @since 4.0.0
     */
    fun getResourcePath(): String

}
