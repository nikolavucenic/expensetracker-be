package com.nv.expensetracker.controllers

import com.nv.expensetracker.controllers.dto.BannerResponse
import com.nv.expensetracker.controllers.dto.BannerType
import com.nv.expensetracker.controllers.dto.SavingGoalBanner
import com.nv.expensetracker.controllers.dto.UpcomingExpenseBanner
import com.nv.expensetracker.controllers.enums.ExpenseType
import com.nv.expensetracker.database.repository.ExpenseFilter
import com.nv.expensetracker.database.repository.ExpenseRepository
import com.nv.expensetracker.database.repository.SavingGoalRepository
import com.nv.expensetracker.database.repository.UserRepository
import org.bson.types.ObjectId
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

@RestController
@RequestMapping("/banner")
class BannerController(
    private val expenseRepository: ExpenseRepository,
    private val savingGoalRepository: SavingGoalRepository,
    private val userRepository: UserRepository,
) {

    @GetMapping
    fun getBanner(): BannerResponse? {
        val userId = ObjectId(SecurityContextHolder.getContext().authentication.principal as String)
        val now = Instant.now()

        findUpcomingExpenseBanner(userId, now)?.let { return it }
        findSavingGoalBanner(userId)?.let { return it }

        val user = userRepository.findById(userId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        }

        if (isNewUser(user.id, now)) {
            return BannerResponse(
                type = BannerType.WELCOME,
                welcomeMessage = "Welcome to ExpenseTracker! Start logging expenses to unlock insights."
            )
        }

        return null
    }

    private fun findUpcomingExpenseBanner(ownerId: ObjectId, now: Instant): BannerResponse? {
        val upcomingExpenses = expenseRepository.search(
            ownerId,
            ExpenseFilter(dateFrom = now, dateTo = now.plus(1, ChronoUnit.DAYS)),
            Sort.by(Sort.Direction.ASC, "date"),
            null
        )
        val nextExpense = upcomingExpenses.firstOrNull() ?: return null

        return BannerResponse(
            type = BannerType.UPCOMING_EXPENSE,
            upcomingExpense = UpcomingExpenseBanner(
                id = nextExpense.id.toHexString(),
                name = nextExpense.name,
                description = nextExpense.description,
                amount = nextExpense.amount,
                date = nextExpense.date,
            )
        )
    }

    private fun findSavingGoalBanner(ownerId: ObjectId): BannerResponse? {
        val goal = savingGoalRepository.findByOwnerId(ownerId) ?: return null
        val savedExpenses = expenseRepository.search(
            ownerId,
            ExpenseFilter(type = ExpenseType.SAVINGS),
            Sort.unsorted(),
            null
        )
        val savedAmount = savedExpenses.sumOf { it.amount }
        val progress = if (goal.targetAmount == 0) 0.0 else savedAmount.toDouble() / goal.targetAmount

        return BannerResponse(
            type = BannerType.SAVING_GOAL,
            savingGoal = SavingGoalBanner(
                id = goal.id.toHexString(),
                name = goal.name,
                savedAmount = savedAmount,
                targetAmount = goal.targetAmount,
                progress = progress,
                targetDate = goal.targetDate,
            )
        )
    }

    private fun isNewUser(userId: ObjectId, now: Instant): Boolean {
        val createdAt = userId.date.toInstant()
        return createdAt.isAfter(now.minus(Duration.ofDays(7)))
    }
}
