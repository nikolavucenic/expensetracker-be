package com.nv.expensetracker.controllers.dto

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.time.Instant

class InstantEpochMillisDeserializer : JsonDeserializer<Instant>() {

    override fun deserialize(parser: JsonParser, context: DeserializationContext): Instant {
        return when (parser.currentToken()) {
            JsonToken.VALUE_NUMBER_INT -> Instant.ofEpochMilli(parser.longValue)
            JsonToken.VALUE_STRING -> {
                val value = parser.text.trim()
                value.toLongOrNull()?.let { Instant.ofEpochMilli(it) } ?: Instant.parse(value)
            }
            else -> context.handleUnexpectedToken(Instant::class.java, parser) as Instant
        }
    }
}
