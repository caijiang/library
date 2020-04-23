package me.jiangcai.common.resource.bean

import me.jiangcai.common.resource.ResourceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile
import java.util.*

/**
 * @author CJ
 */
@Controller
open class ResourceController(
    @Autowired
    private val resourceService: ResourceService
) {
    private fun getFileNameSuffix(originalName: String, contentType: String): String {
        return try {
            originalName.substring(originalName.lastIndexOf(".") + 1)
        } catch (var4: Exception) {
            MediaType.parseMediaType(contentType).subtype
        }
    }

    @Suppress("MVCPathVariableInspection")
    @PostMapping("\${jiangcai.resource.upload.temp.uri:/uploadTempResource}")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT','ROLE_UPLOAD_TEMP')")
    @ResponseBody
    fun uploadTempResource(data: MultipartFile): Any {
        val name = "temp/" + UUID.randomUUID().toString().replace("-", "") + "." + getFileNameSuffix(
            data.originalFilename,
            data.contentType
        )
        val resource = resourceService.uploadResource(name, data.inputStream)
        return mapOf(
            "path" to name,
            "previewUrl" to resource.httpUrl().toString()
        )
    }
}