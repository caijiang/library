package me.jiangcai.common.resource.impl

import me.jiangcai.common.resource.Resource
import me.jiangcai.common.resource.bean.VFSHelper
import org.apache.commons.logging.LogFactory
import org.apache.commons.vfs2.FileObject
import org.springframework.core.io.AbstractResource
import org.springframework.core.io.WritableResource
import org.springframework.util.StreamUtils
import java.io.*
import java.net.URI
import java.net.URISyntaxException
import java.net.URL

/**
 * @author CJ
 */
class VFSResource(
    private val resourcePath: String,
    private val helper: VFSHelper,
    private val fileName: String,
    private val uri: URI
) : AbstractResource(),
    Resource, WritableResource {

    private val log = LogFactory.getLog(VFSResource::class.java)

    @Throws(IOException::class)
    fun <T> accessFileObject(function: ((FileObject) -> T)): T? {
        return helper.handle(fileName, function)
    }

    override fun exists(): Boolean {
        return try {
            this.accessFileObject { it.exists() }!!
        } catch (e: Throwable) {
            log.error("exists", e)
            false
        }

    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return accessFileObject { fileObject -> fileObject.content.size }!!.toLong()
    }

    @Throws(IOException::class)
    override fun lastModified(): Long {
        return accessFileObject { fileObject -> fileObject.content.lastModifiedTime }!!.toLong()
    }

    @Throws(IOException::class)
    override fun getFile(): File {
        return File(fileName)
    }

    override fun getFilename(): String {
        return super.getFilename()
    }

    @Throws(IOException::class)
    override fun getURI(): URI {
        try {
            return url.toURI()
        } catch (e: URISyntaxException) {
            throw IllegalArgumentException("bad VFS file:$fileName", e)
        }

    }

    @Throws(IOException::class)
    override fun getURL(): URL {
        return URL(fileName)
    }

    override fun isWritable(): Boolean {
        return try {
            accessFileObject { it.isWriteable }!!
        } catch (e: Throwable) {
            log.error("isWritable", e)
            false
        }

    }

    @Throws(IOException::class)
    override fun getOutputStream(): OutputStream {
        return object : ByteArrayOutputStream() {
            @Throws(IOException::class)
            override fun close() {
                accessFileObject { fileObject ->
                    fileObject.content.outputStream.use {
                        StreamUtils.copy(buf, it)
                        it.flush()
                    }
                    null
                }
            }
        }
    }

    override fun getDescription(): String {
        return fileName
    }

    @Throws(IOException::class)
    override fun getInputStream(): InputStream {
        return accessFileObject { fileObject ->
            val inputStream = fileObject.content.inputStream
            val byteArrayOutputStream = ByteArrayOutputStream()
            StreamUtils.copy(inputStream, byteArrayOutputStream)
            inputStream.close()
            ByteArrayInputStream(byteArrayOutputStream.toByteArray())
        }!!

    }

    @Throws(IOException::class)
    override fun httpUrl(): URL {
        return uri.toURL()
    }

    override fun getResourcePath(): String {
        return resourcePath
    }
}