package me.jiangcai.common.resource.bean

import me.jiangcai.common.resource.Resource
import me.jiangcai.common.resource.ResourceService
import org.springframework.beans.factory.annotation.Autowired
import javax.persistence.AttributeConverter
import javax.persistence.Converter

/**
 * @author CJ
 */
@Converter(autoApply = true)
class ResourceJpaConverter(
    @Autowired
    private val resourceService: ResourceService
) : AttributeConverter<Resource, String> {

    override fun convertToDatabaseColumn(attribute: Resource?): String? {
        return attribute?.getResourcePath()
    }

    override fun convertToEntityAttribute(dbData: String?): Resource? {
        return dbData?.let { resourceService.getResource(it) }
    }
}