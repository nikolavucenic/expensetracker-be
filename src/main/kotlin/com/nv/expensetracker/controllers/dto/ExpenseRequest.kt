package com.nv.expensetracker.controllers.dto

import com.nv.expensetracker.controllers.enums.ExpenseType
import jakarta.validation.constraints.NotBlank
import java.time.Instant

data class ExpenseRequest(
    val id: String?,
    @field:NotBlank(message = "Expense name can't be empty.")
    val name: String,
    val description: String?,
    @field:NotBlank(message = "Expense amount can't be empty.")
    val amount: Int,
    val type: ExpenseType = ExpenseType.UNKNOWN,
    val date: Instant = Instant.now(),
    val isRecurring: Boolean = false,
)