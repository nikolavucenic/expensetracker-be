package com.nv.expensetracker.controllers

import com.nv.expensetracker.controllers.dto.CategoryOptionResponse
import com.nv.expensetracker.controllers.enums.ExpenseCategory
import com.nv.expensetracker.controllers.enums.ExpenseType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/categories")
class CategoryController {

    @GetMapping
    fun getCategoryOptions(): List<CategoryOptionResponse> =
        ExpenseType.entries.map { type ->
            val category = type.category
            CategoryOptionResponse(
                type = type,
                category = category,
                label = category.toDisplayLabel(),
                colorHex = category.toColorHex(),
            )
        }

    private fun ExpenseCategory.toDisplayLabel(): String = when (this) {
        ExpenseCategory.ESSENTIALS -> "Essentials"
        ExpenseCategory.FOOD_AND_DRINK -> "Food & Drink"
        ExpenseCategory.TRAVEL -> "Travel"
        ExpenseCategory.HEALTH -> "Health"
        ExpenseCategory.EDUCATION -> "Education"
        ExpenseCategory.HOME -> "Home"
        ExpenseCategory.PERSONAL -> "Personal"
        ExpenseCategory.ENTERTAINMENT -> "Entertainment"
        ExpenseCategory.MISC -> "Misc"
    }

    private fun ExpenseCategory.toColorHex(): String = when (this) {
        ExpenseCategory.ESSENTIALS -> "#2563EB"
        ExpenseCategory.FOOD_AND_DRINK -> "#F59E0B"
        ExpenseCategory.TRAVEL -> "#06B6D4"
        ExpenseCategory.HEALTH -> "#22C55E"
        ExpenseCategory.EDUCATION -> "#7C3AED"
        ExpenseCategory.HOME -> "#EA580C"
        ExpenseCategory.PERSONAL -> "#EC4899"
        ExpenseCategory.ENTERTAINMENT -> "#8B5CF6"
        ExpenseCategory.MISC -> "#6B7280"
    }
}
