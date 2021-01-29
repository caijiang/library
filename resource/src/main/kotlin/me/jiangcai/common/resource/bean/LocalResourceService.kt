package me.jiangcai.common.resource.bean

import me.jiangcai.common.resource.Resource
import me.jiangcai.common.resource.impl.LocalResource
import org.springframework.core.env.Environment
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

/**
 * 本地提供的资源服务
 * @author CJ
 */
class LocalResourceService(
    environment: Environment,
    private val fileHome: String
) : AbstractResourceService() {

    private val urlPrefix: String

    init {
        val host = environment.getProperty("jiangcai.resource.host", "localhost")
        val port = environment.getProperty("server.port", Int::class.java, 8080)
        val suffix =
            if (port == 80)
                ""
            else
                ":${port}"
        urlPrefix = "http://${host}$suffix${VFSResourceService.ServletContextResourcePath}/"
    }

    private fun getLocalPath(path: String): Path {
        val file = File(fileHome + File.separator + path)
        return Paths.get(file.toURI())
    }

    override fun uploadResource(path: String, data: InputStream): Resource {
        val path1 = getLocalPath(path)
        Files.createDirectories(path1.parent)
        Files.copy(data, path1, StandardCopyOption.REPLACE_EXISTING)
        return getResource(path)
    }

    override fun moveResource(path: String, fromPath: String): Resource {
        val path1 = getLocalPath(path)
        Files.createDirectories(path1.parent)
        Files.move(getLocalPath(fromPath), path1, StandardCopyOption.REPLACE_EXISTING)
        return getResource(path)
    }

    override fun getResource(path: String): Resource {
        return LocalResource(path, fileHome + File.separator + path, urlPrefix + path)
    }

    override fun deleteResource(path: String) {
        val ioPath = Paths.get(fileHome + File.separator + path)
        Files.deleteIfExists(ioPath)
    }
}