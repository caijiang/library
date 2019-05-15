//package me.jiangcai.common.jpa.entity
//
//import com.fasterxml.jackson.databind.ObjectMapper
//import javax.persistence.AttributeConverter
//import javax.persistence.Converter
//
//@Converter(autoApply = true)
//class GoodThingConverter
////    : JSONStoringConverter<GoodThing>(
////    GoodThing::class.java
////)
//    : AttributeConverter<GoodThing, String> {
//    override fun convertToDatabaseColumn(p0: GoodThing?): String? {
//        return p0?.let {
//            ObjectMapper().writeValueAsString(it)
//        }
//    }
//
//    override fun convertToEntityAttribute(p0: String?): GoodThing? {
//        return p0.let {
//            ObjectMapper().readValue(it, GoodThing::class.java)
//        }
//    }
//
//}