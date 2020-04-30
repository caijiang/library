package me.jiangcai.common.resource.impl

import com.aliyun.oss.OSS
import me.jiangcai.common.resource.Resource
import org.springframework.core.io.AbstractResource
import org.springframework.core.io.WritableResource
import java.io.*
import java.net.URL

/**
 * @author CJ
 */
class OSSResource(
    private val client: OSS,
    private val bucketName: String,
    private val path: String,
    private val urlPrefix: String
) : AbstractResource(), Resource, WritableResource {

    override fun httpUrl(): URL {
        return URL(urlPrefix + path)
    }

    override fun getResourcePath(): String = path

    override fun getDescription(): String = "oss resource of $path"

    override fun exists(): Boolean {
        return try {
            client.getObjectMetadata(bucketName, path) != null
        } catch (e: Exception) {
            false
        }
    }

    override fun contentLength(): Long {
        return client.getObjectMetadata(bucketName, path).contentLength
    }

    override fun lastModified(): Long {
        return client.getObjectMetadata(bucketName, path).lastModified.time
    }

    override fun getFilename(): String {
        return path
    }

    override fun getURL(): URL {
        return httpUrl()
    }

    override fun getInputStream(): InputStream {
        return client.getObject(bucketName, path)
            .use {
                ByteArrayInputStream(it.objectContent.readBytes())
            }
    }

    override fun isWritable(): Boolean = true

    override fun getOutputStream(): OutputStream {
        return object : ByteArrayOutputStream() {
            @Throws(IOException::class)
            override fun close() {
                client.putObject(bucketName, path, ByteArrayInputStream(buf))
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OSSResource) return false
//        if (!super.equals(other)) return false

        if (bucketName != other.bucketName) return false
        if (path != other.path) return false

        return true
    }

    override fun hashCode(): Int {
        var result = 0
        result = 31 * result + bucketName.hashCode()
        result = 31 * result + path.hashCode()
        return result
    }
}