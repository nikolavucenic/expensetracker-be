package com.nv.expensetracker.controllers.dto

import com.nv.expensetracker.controllers.enums.ExpenseCategory
import com.nv.expensetracker.controllers.enums.ExpenseType

data class CategoryOptionResponse(
    val type: ExpenseType,
    val category: ExpenseCategory,
    val label: String,
    val colorHex: String,
)
