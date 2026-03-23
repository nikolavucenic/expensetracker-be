package com.nv.expensetracker.controllers.dto

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.time.Instant

data class ExpenseResponse(
    val id: String,
    val name: String,
    val description: String?,
    val amount: Int,
    val type: String,
    val category: String,
    @field:JsonSerialize(using = InstantEpochMillisSerializer::class)
    val date: Instant,
    val isRecurring: Boolean,
)
