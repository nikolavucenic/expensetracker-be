package com.nv.expensetracker.database.repository

import com.nv.expensetracker.controllers.enums.ExpenseType
import java.time.Instant

/**
 * Filter parameters for searching expenses.
 */
data class ExpenseFilter(
    val type: ExpenseType? = null,
    val date: Instant? = null,
    val dateFrom: Instant? = null,
    val dateTo: Instant? = null,
    val amount: Int? = null,
    val amountFrom: Int? = null,
    val amountTo: Int? = null,
    val isRecurring: Boolean? = null,
    val search: String? = null,
)
