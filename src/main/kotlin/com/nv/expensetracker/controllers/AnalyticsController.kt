package com.nv.expensetracker.controllers

import com.nv.expensetracker.controllers.dto.AnalyticsResponse
import com.nv.expensetracker.controllers.dto.CategoryPercentage
import com.nv.expensetracker.controllers.dto.DailyTotal
import com.nv.expensetracker.controllers.dto.ExpenseDay
import com.nv.expensetracker.controllers.dto.TypeDelta
import com.nv.expensetracker.controllers.dto.ExpenseResponse
import com.nv.expensetracker.controllers.enums.ExpenseCategory
import com.nv.expensetracker.controllers.enums.ExpenseType
import com.nv.expensetracker.database.repository.ExpenseRepository
import org.bson.types.ObjectId
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@RestController
@RequestMapping("/analytics")
class AnalyticsController(
    private val repository: ExpenseRepository,
) {

    @GetMapping
    fun getAnalytics(): AnalyticsResponse {
        val ownerId = ObjectId(SecurityContextHolder.getContext().authentication.principal as String)
        val expenses = repository.findByOwnerId(ownerId)
        return calculate(expenses)
    }

    private fun calculate(expenses: List<com.nv.expensetracker.database.model.Expense>): AnalyticsResponse {
        val now = LocalDate.now(ZoneOffset.UTC)
        val startOfMonth = now.withDayOfMonth(1).atStartOfDay(ZoneOffset.UTC).toInstant()
        val startOfNextMonth = now.plusMonths(1).withDayOfMonth(1).atStartOfDay(ZoneOffset.UTC).toInstant()
        val startOfPrevMonth = now.minusMonths(1).withDayOfMonth(1).atStartOfDay(ZoneOffset.UTC).toInstant()

        val thisMonthExpenses = expenses.filter { it.date >= startOfMonth && it.date < startOfNextMonth }
        val lastMonthExpenses = expenses.filter { it.date >= startOfPrevMonth && it.date < startOfMonth }

        val totalAmount = thisMonthExpenses.sumOf { it.amount }
        val totalsByCategory = thisMonthExpenses.groupBy { it.category }.mapValues { it.value.sumOf { e -> e.amount } }
        val categoryPercentages = ExpenseCategory.values().map { category ->
            val amount = totalsByCategory[category] ?: 0
            val percent = if (totalAmount == 0) 0.0 else amount.toDouble() * 100 / totalAmount
            CategoryPercentage(category, percent)
        }

        val totalsThisMonthByType = thisMonthExpenses.groupBy { it.type }.mapValues { it.value.sumOf { e -> e.amount } }
        val totalsAllTimeByType = expenses.groupBy { it.type }.mapValues { it.value.sumOf { e -> e.amount } }
        val totalsLastMonthByType = lastMonthExpenses.groupBy { it.type }.mapValues { it.value.sumOf { e -> e.amount } }

        val monthVsTotal = ExpenseType.values().map { type ->
            val thisMonth = totalsThisMonthByType[type] ?: 0
            val total = totalsAllTimeByType[type] ?: 0
            val delta = if (total == 0) if (thisMonth == 0) 0.0 else 100.0 else (thisMonth - total) * 100.0 / total
            TypeDelta(type, delta)
        }

        val monthVsLastMonth = ExpenseType.values().map { type ->
            val thisMonth = totalsThisMonthByType[type] ?: 0
            val lastMonth = totalsLastMonthByType[type] ?: 0
            val delta = if (lastMonth == 0) if (thisMonth == 0) 0.0 else 100.0 else (thisMonth - lastMonth) * 100.0 / lastMonth
            TypeDelta(type, delta)
        }

        val recurringExpenses = expenses.filter { it.isRecurring }.map { it.toResponse() }

        val largestExpenseThisMonth = thisMonthExpenses.maxByOrNull { it.amount }?.toResponse()

        val dayWithLargestSum = expenses.groupBy { it.date.atZone(ZoneOffset.UTC).toLocalDate() }
            .mapValues { it.value.sumOf { e -> e.amount } }
            .maxByOrNull { it.value }
            ?.let { ExpenseDay(it.key, it.value) }

        val monthsLeft = 12 - now.monthValue
        val monthlySubscriptions = expenses.filter { it.type == ExpenseType.SUBSCRIPTIONS && it.isRecurring }.sumOf { it.amount }
        val subscriptionSavingsEstimate = monthlySubscriptions * monthsLeft

        val daysInMonth = now.lengthOfMonth()
        val dailyTotals = (1..daysInMonth).map { day ->
            val date = now.withDayOfMonth(day)
            val start = date.atStartOfDay(ZoneOffset.UTC).toInstant()
            val end = date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant()
            val total = thisMonthExpenses.filter { it.date >= start && it.date < end }.sumOf { it.amount }
            DailyTotal(date, total)
        }

        return AnalyticsResponse(
            categoryPercentages = categoryPercentages,
            monthVsTotal = monthVsTotal,
            monthVsLastMonth = monthVsLastMonth,
            recurringExpenses = recurringExpenses,
            largestExpenseThisMonth = largestExpenseThisMonth,
            dayWithLargestSum = dayWithLargestSum,
            subscriptionSavingsEstimate = subscriptionSavingsEstimate,
            dailyTotals = dailyTotals,
        )
    }

    private fun com.nv.expensetracker.database.model.Expense.toResponse(): ExpenseResponse =
        ExpenseResponse(
            id = id.toHexString(),
            name = name,
            description = description,
            amount = amount,
            type = type,
            category = category,
            date = date,
            isRecurring = isRecurring,
        )
}
