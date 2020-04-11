package me.jiangcai.common.resource.bean

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import me.jiangcai.common.resource.Resource
import me.jiangcai.common.resource.ResourceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @author CJ
 */
@Component
class ResourceJsonModule(
    @Autowired
    private val resourceService: ResourceService
) : SimpleModule("Resource", Version(1, 0, 0, "", "", "")) {

    class ResourceSerializer : StdSerializer<Resource>(Resource::class.java) {
        override fun serialize(value: Resource, gen: JsonGenerator?, provider: SerializerProvider?) {
            gen?.writeStartObject()
            gen?.writeObjectField("path", value.getResourcePath())
            gen?.writeObjectField("url", value.httpUrl().toString())
            gen?.writeEndObject()
        }

    }

    inner class ResourceDeserializer : StdDeserializer<Resource>(Resource::class.java) {
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): Resource {
            return if (p.currentToken == JsonToken.START_OBJECT) {
                // read whole object
                val path = p.readValueAs(JsonNode::class.java)["path"]
                if (path != null && path.isTextual)
                    return resourceService.getResource(path.textValue())
                throw IllegalArgumentException("bad input for Resource.")
            } else {
                resourceService.getResource(p.valueAsString)
            }
        }

    }

    override fun setupModule(context: SetupContext) {
        super.setupModule(context)

        context.addBeanSerializerModifier(object : BeanSerializerModifier() {
            override fun modifySerializer(
                config: SerializationConfig?,
                beanDesc: BeanDescription,
                serializer: JsonSerializer<*>?
            ): JsonSerializer<*> {
                if (Resource::class.java.isAssignableFrom(beanDesc.beanClass)) {
                    return ResourceSerializer()
                }
                return super.modifySerializer(config, beanDesc, serializer)
            }
        })
        context.addBeanDeserializerModifier(object : BeanDeserializerModifier() {
            override fun modifyDeserializer(
                config: DeserializationConfig?,
                beanDesc: BeanDescription,
                deserializer: JsonDeserializer<*>?
            ): JsonDeserializer<*> {
                if (Resource::class.java.isAssignableFrom(beanDesc.beanClass)) {
                    return ResourceDeserializer()
                }
                return super.modifyDeserializer(config, beanDesc, deserializer)
            }
        })
//        addSerializer(ResourceSerializer())
//        addDeserializer(Resource::class.java, ResourceDeserializer())
    }
}