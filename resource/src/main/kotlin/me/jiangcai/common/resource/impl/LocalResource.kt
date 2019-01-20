package me.jiangcai.common.resource.impl

import me.jiangcai.common.resource.Resource
import org.springframework.core.io.FileSystemResource
import java.net.URL

/**
 * @author CJ
 */
class LocalResource(
    private val resourcePath: String, filePath: String, private val httpUrl: String
) : FileSystemResource(filePath), Resource {
    override fun httpUrl(): URL {
        return URL(httpUrl)
    }

    override fun getResourcePath(): String = resourcePath
}