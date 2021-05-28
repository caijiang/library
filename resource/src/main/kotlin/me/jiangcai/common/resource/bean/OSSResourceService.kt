package me.jiangcai.common.resource.bean

import com.aliyun.oss.OSS
import com.aliyun.oss.OSSClientBuilder
import me.jiangcai.common.resource.Resource
import me.jiangcai.common.resource.impl.OSSResource
import org.springframework.core.env.Environment
import java.io.ByteArrayInputStream
import java.io.InputStream

/**
 * @author CJ
 */
class OSSResourceService(environment: Environment) : AbstractResourceService() {

    private val urlPrefix: String = if (environment.getProperty("com.aliyun.oss.urlPrefix")!!.endsWith("/"))
        environment.getProperty("com.aliyun.oss.urlPrefix")!!
    else {
        environment.getProperty("com.aliyun.oss.urlPrefix")!! + "/"
    }
    private val bucketName: String = environment.getProperty("com.aliyun.oss.bucketName")!!
    private val client: OSS = OSSClientBuilder()
        .build(
            environment.getProperty("com.aliyun.oss.endpoint"),
            environment.getProperty("com.aliyun.accessKeyId"),
            environment.getProperty("com.aliyun.secretAccessKey")
        )


    override fun uploadResource(path: String, data: InputStream): Resource {
        client.putObject(bucketName, path, data)
        return getResource(path)
    }

    override fun moveResource(path: String, fromPath: String): Resource {
        val buf = getResource(fromPath).inputStream.use { it.readBytes() }
        deleteResource(fromPath)
        return uploadResource(path, ByteArrayInputStream(buf))
    }

    override fun getResource(path: String): Resource {
        return OSSResource(client, bucketName, path, urlPrefix)
    }

    override fun deleteResource(path: String) {
        client.deleteObject(bucketName, path)
    }


}