package me.jiangcai.common.ext.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.JsonTokenId
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.DeserializationFeature
import java.time.DateTimeException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


/**
 * @author CJ
 */
class LocalDateTimeDeserializer
    : com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME) {

    private val defaultFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override fun deserialize(parser: JsonParser, context: DeserializationContext): LocalDateTime? {
        if (parser.hasTokenId(JsonTokenId.ID_STRING)) {
            val string = parser.text.trim { it <= ' ' }
            if (string.isEmpty()) {
                return null
            }

            try {
                if (_formatter == defaultFormatter) {
                    // JavaScript by default includes time and zone in JSON serialized Dates (UTC/ISO instant format).
                    if (string.length > 10 && string[10] == 'T') {
                        return if (string.endsWith("Z")) {
                            LocalDateTime.ofInstant(Instant.parse(string), ZoneId.systemDefault())
                        } else {
                            LocalDateTime.parse(string, defaultFormatter)
                        }
                    }
                }

                return LocalDateTime.parse(string, _formatter)
            } catch (e: DateTimeException) {
                _rethrowDateTimeException<Any>(parser, context, e, string)
            }

        }
        if (parser.isExpectedStartArrayToken) {
            if (parser.nextToken() == JsonToken.END_ARRAY) {
                return null
            }
            val year = parser.intValue
            val month = parser.nextIntValue(-1)
            val day = parser.nextIntValue(-1)
            val hour = parser.nextIntValue(-1)
            val minute = parser.nextIntValue(-1)

            if (parser.nextToken() != JsonToken.END_ARRAY) {
                val second = parser.intValue

                if (parser.nextToken() != JsonToken.END_ARRAY) {
                    var partialSecond = parser.intValue
                    if (partialSecond < 1000 && !context.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS))
                        partialSecond *= 1000000 // value is milliseconds, convert it to nanoseconds

                    if (parser.nextToken() != JsonToken.END_ARRAY) {
                        throw context.wrongTokenException(parser, JsonToken.END_ARRAY, "Expected array to end.")
                    }
                    return LocalDateTime.of(year, month, day, hour, minute, second, partialSecond)
                }
                return LocalDateTime.of(year, month, day, hour, minute, second)
            }
            return LocalDateTime.of(year, month, day, hour, minute)
        }
        if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
            return parser.embeddedObject as LocalDateTime
        }
        throw context.wrongTokenException(parser, JsonToken.VALUE_STRING, "Expected array or string.")
    }
}