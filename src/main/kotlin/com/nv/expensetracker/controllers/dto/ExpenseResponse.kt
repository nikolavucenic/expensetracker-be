package com.nv.expensetracker.controllers.dto

import com.nv.expensetracker.controllers.enums.ExpenseCategory
import com.nv.expensetracker.controllers.enums.ExpenseType
import java.time.Instant

data class ExpenseResponse(
    val id: String,
    val name: String,
    val description: String?,
    val amount: Int,
    val type: ExpenseType,
    val category: ExpenseCategory,
    val date: Instant,
    val isRecurring: Boolean,
)
