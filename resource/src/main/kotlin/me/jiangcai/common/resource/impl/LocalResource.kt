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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LocalResource) return false
//        if (!super.equals(other)) return false

        if (resourcePath != other.resourcePath) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + resourcePath.hashCode()
        return result
    }


}