package me.jiangcai.common.resource.bean

import me.jiangcai.common.resource.DevelopmentConfig
import me.jiangcai.common.resource.Resource
import me.jiangcai.common.resource.impl.LocalResource
import me.jiangcai.common.resource.impl.VFSResource
import org.apache.commons.logging.LogFactory
import org.apache.commons.vfs2.FileSystemException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import org.springframework.web.context.WebApplicationContext
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.URI
import java.net.URISyntaxException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

/**
 * @author CJ
 */
@Suppress("SpringJavaAutowiredMembersInspection")
class VFSResourceService : AbstractResourceService {

    companion object {
        const val ServletContextResourcePath = "/_resources"
    }

    private val log = LogFactory.getLog(VFSResourceService::class.java)

    private val uriPrefix: URI
    private var localFileMode: Boolean = false
    private var fileHome: URI? = null
    private var fileFile: File? = null

    @Autowired
    private lateinit var vfsHelper: VFSHelper

    //    @Autowired(required = false)
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    constructor(environment: Environment, webApplicationContext: WebApplicationContext?)
            : this(
        environment.acceptsProfiles(Profiles.of("development")),
        environment.getProperty("jiangcai.resource.host", "localhost"),
        environment.getProperty(
            "jiangcai.resource.http.uri",
            environment.getProperty("me.jiangcai.lib.resource.http.uri")
        ),
        environment.getProperty(
            "jiangcai.resource.home",
            environment.getProperty("me.jiangcai.lib.resource.home")
        ),
        environment.getProperty("server.port", Int::class.java, 8080),
        webApplicationContext,
        "jiangcai.resource"
    )

    /**
     * @param uri                   要求使用的资源http访问uri,为null表示不用
     * @param home                  要求使用的资源位置,为null表示不用
     * @param port                  该应用的部署port,用于将资源管理在部署目录
     * @param webApplicationContext webApplicationContext
     * @param prefix                系统属性的前缀,保留应用中存在多个资源管理系统的可能
     */
    constructor(
        development: Boolean,
        developmentHost: String,
        uri: String?,
        home: String?,
        port: Int,
        webApplicationContext: WebApplicationContext?,
        prefix: String
    ) {
        // 应该先构建字符串,再根据值判断是否未本地系统(file)
        val autoHome = uri.isNullOrEmpty() || home.isNullOrEmpty()
        var newHome: String
        var newUri: String
        if (autoHome) {
            val suffix =
                if (port == 80)
                    ""
                else
                    ":${port}"

            if (development) {
                log.warn("ResourceService working in Development mode.")
                newUri = "http://${developmentHost}$suffix$ServletContextResourcePath"
                newHome = DevelopmentConfig.getResourcesHome()
            } else {
                if (webApplicationContext == null)
                    throw IllegalStateException("ResourceService required web Environment.")
                newUri = try {
                    "http://" + webApplicationContext.servletContext.virtualServerName + suffix + ServletContextResourcePath
                } catch (ignored: Throwable) {
                    log.warn("ResourceService can not use getVirtualServerName in Servlet Version < 3.1. ")
                    "http://localhost$suffix$ServletContextResourcePath"
                }

                newHome = webApplicationContext.servletContext.getRealPath(ServletContextResourcePath)
                log.warn(
                    "ResourceService running in ServletContextPath, please setup " + prefix + ".http.uri" + ","
                            + prefix + ".home to define VFS ResourceService"
                )

            }

        } else {
            newHome = home!!
            newUri = uri!!
        }

        if (!newHome.endsWith("/"))
            newHome = "$newHome/"

        // 研究home
        try {
            val homeUri = URI(newHome)
            if ("file" == homeUri.scheme || homeUri.scheme == null) {
                localFileMode = true
                fileHome = null
                fileFile = File(homeUri.path)
            } else {
                localFileMode = false
                fileFile = null
                fileHome = homeUri
            }
        } catch (e: URISyntaxException) {
            if (autoHome) {
                localFileMode = true
                fileHome = null
                fileFile = File(newHome)
            } else
                throw IllegalArgumentException(e)
        }

        if (!newUri.endsWith("/"))
            newUri = "$newUri/"


        log.info("ResourceService running on $newHome, via:$newUri")

        try {
            uriPrefix = URI(newUri)

        } catch (e: URISyntaxException) {
            throw IllegalArgumentException(e)
        }

    }

    @Throws(IOException::class)
    override fun uploadResource(path: String, data: InputStream): Resource {
        if (path.startsWith("/"))
            throw IllegalArgumentException("bad resource path:$path")
        // 检查是否是本地文件系统,如果是的话就使用本地文件系统技术
        if (localFileMode) {
            val path1 = getLocalPath(path)
            Files.createDirectories(path1.parent)
            Files.copy(data, path1, StandardCopyOption.REPLACE_EXISTING)
            return getResource(path)
        }

        val filePath = fileHome!!.toString() + path

        data.use { input ->
            vfsHelper.handle(filePath) { file ->
                file.content.outputStream.use { out ->
                    try {
                        input.copyTo(out)
                        out.flush()
                    } catch (e: IOException) {
                        throw FileSystemException(e)
                    }
                }
                null
            }
        }
        return getResource(path)
    }

    @Throws(IOException::class)
    override fun moveResource(path: String, fromPath: String): Resource {
        if (path.startsWith("/"))
            throw IllegalArgumentException("bad resource path:$path")
        // 检查是否是本地文件系统,如果是的话就使用本地文件系统技术
        if (localFileMode) {
            val path1 = getLocalPath(path)
            Files.createDirectories(path1.parent)
            Files.move(getLocalPath(fromPath), path1, StandardCopyOption.REPLACE_EXISTING)
            return getResource(path)
        }

        val filePath = fileHome!!.toString() + path

        vfsHelper.handle(fileHome!!.toString() + fromPath) { fromFile ->
            vfsHelper.handle(filePath) { toFile ->
                fromFile.moveTo(toFile)
                null
            }
            null
        }

        return getResource(path)
    }

    private fun getLocalPath(path: String): Path {
        val file = File(fileFile!!.toString() + File.separator + path)
        return Paths.get(file.toURI())
    }

    override fun getResource(path: String): Resource {
        if (path.startsWith("/"))
            throw IllegalArgumentException("bad resource path:$path")

        val url = uriPrefix.toString() + path

        if (localFileMode) {
            return LocalResource(path, fileFile!!.toString() + File.separator + path, url)
        }
        val filePath = fileHome!!.toString() + path

        return try {
            VFSResource(path, vfsHelper, filePath, URI(url))
        } catch (e: URISyntaxException) {
            log.error("解释资源时", e)
            throw IOException("资源格式", e)
        }

    }

    @Throws(IOException::class)
    override fun deleteResource(path: String) {
        if (path.startsWith("/"))
            throw IllegalArgumentException("bad resource path:$path")

        if (localFileMode) {
            val ioPath = Paths.get(fileFile!!.toString() + File.separator + path)
            Files.deleteIfExists(ioPath)
            return
        }

        val filePath = fileHome!!.toString() + path

        vfsHelper.handle(filePath) {
            it.delete()
        }
    }
}