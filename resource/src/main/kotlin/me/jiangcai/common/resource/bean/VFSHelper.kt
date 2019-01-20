package me.jiangcai.common.resource.bean

import org.apache.commons.vfs2.FileObject
import org.apache.commons.vfs2.FileSystem
import org.apache.commons.vfs2.FileSystemManager
import org.apache.commons.vfs2.FileSystemOptions
import org.apache.commons.vfs2.impl.StandardFileSystemManager
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder
import org.springframework.stereotype.Component
import java.io.IOException

/**
 * @author CJ
 */
@Component
class VFSHelper {

    private val options = FileSystemOptions()
    private var passive: Boolean = false

    init {
        SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(options, false)
        SftpFileSystemConfigBuilder.getInstance().setTimeout(options, 30000)

        FtpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(options, false)
        FtpFileSystemConfigBuilder.getInstance().setDataTimeout(options, 30000)
        FtpFileSystemConfigBuilder.getInstance().setSoTimeout(options, 30000)
        FtpFileSystemConfigBuilder.getInstance().setPassiveMode(options, passive)
    }

    @Throws(IOException::class)
    fun <R> handle(name: String, function: ((FileObject) -> R)?): R? {
        val manager = StandardFileSystemManager()
        manager.init()
        var fileSystem: FileSystem? = null
        try {
            val file = resolveFile(name, manager)
            fileSystem = file.fileSystem
            return try {
                if (function != null) function(file) else null
            } catch (ex: FileSystemException) {
                togglePassive()
                function!!(file)
            } finally {
                file.close()
            }
        } finally {
            if (fileSystem != null)
                manager.closeFileSystem(fileSystem)
            manager.close()
        }
    }

    @Throws(FileSystemException::class)
    fun resolveFile(name: String, manager: FileSystemManager): FileObject {
        return manager.resolveFile(name, options)
    }

    private fun togglePassive() {
        passive = !passive
        FtpFileSystemConfigBuilder.getInstance().setPassiveMode(options, passive)
    }
}