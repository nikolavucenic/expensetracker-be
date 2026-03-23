package com.nv.expensetracker.controllers.dto

import java.time.LocalDate

data class CategoryPercentage(
    val category: String,
    val percent: Double,
)

data class TypeDelta(
    val type: String,
    val percent: Double,
)

data class ExpenseDay(
    val date: LocalDate,
    val total: Int,
)

data class DailyTotal(
    val date: LocalDate,
    val total: Int,
)

data class AnalyticsResponse(
    val categoryPercentages: List<CategoryPercentage>,
    val monthVsTotal: List<TypeDelta>,
    val monthVsLastMonth: List<TypeDelta>,
    val recurringExpenses: List<ExpenseResponse>,
    val largestExpenseThisMonth: ExpenseResponse?,
    val dayWithLargestSum: ExpenseDay?,
    val subscriptionSavingsEstimate: Int,
    val dailyTotals: List<DailyTotal>,
)
