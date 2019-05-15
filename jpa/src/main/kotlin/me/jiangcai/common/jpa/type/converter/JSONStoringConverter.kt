package me.jiangcai.common.jpa.type.converter

import com.fasterxml.jackson.databind.ObjectMapper
import me.jiangcai.common.jpa.type.JSONStoring
import javax.persistence.AttributeConverter
import javax.persistence.Converter

/**
 * @author CJ
 */
@Converter(autoApply = true)
class JSONStoringConverter : AttributeConverter<JSONStoring, String> {
    private val objectMapper = ObjectMapper()
    override fun convertToDatabaseColumn(attribute: JSONStoring?): String? {
        return attribute?.let {
            objectMapper.writeValueAsString(it.data)
        }
    }

    override fun convertToEntityAttribute(dbData: String?): JSONStoring? {
        return dbData?.let {
            try {
                val x = objectMapper.readValue(it, Map::class.java)
                return JSONStoring((x))
            } catch (e: Throwable) {
                println("this is bug??")
                throw e
            }
        }
    }
}