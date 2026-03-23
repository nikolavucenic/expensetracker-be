package com.nv.expensetracker.controllers.dto

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.nv.expensetracker.controllers.enums.ExpenseType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.Instant

data class ExpenseRequest(
    val id: String?,
    @field:NotBlank(message = "Expense name can't be empty.")
    val name: String,
    val description: String?,
    @field:NotNull(message = "Expense amount can't be empty.")
    val amount: Int,
    val type: String = ExpenseType.UNKNOWN.name,
    @field:JsonDeserialize(using = InstantEpochMillisDeserializer::class)
    val date: Instant = Instant.now(),
    val isRecurring: Boolean = false,
)
