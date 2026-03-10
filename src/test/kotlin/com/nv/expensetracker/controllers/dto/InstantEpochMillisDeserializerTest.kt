package com.nv.expensetracker.controllers.dto

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant

class InstantEpochMillisDeserializerTest {

    private val objectMapper = JsonMapper.builder()
        .addModule(KotlinModule.Builder().build())
        .addModule(JavaTimeModule())
        .build()

    @Test
    fun `expense request accepts epoch millis as long`() {
        val epochMillis = 1735689600000L

        val request = objectMapper.readValue(
            """
            {
              "name": "Coffee",
              "amount": 5,
              "type": "UNKNOWN",
              "date": $epochMillis,
              "isRecurring": false
            }
            """.trimIndent(),
            ExpenseRequest::class.java
        )

        assertEquals(Instant.ofEpochMilli(epochMillis), request.date)
    }

    @Test
    fun `saving goal request accepts epoch millis as string`() {
        val epochMillis = 1735689600000L

        val request = objectMapper.readValue(
            """
            {
              "name": "Trip",
              "targetAmount": 1000,
              "targetDate": "$epochMillis"
            }
            """.trimIndent(),
            SavingGoalRequest::class.java
        )

        assertEquals(Instant.ofEpochMilli(epochMillis), request.targetDate)
    }
}
