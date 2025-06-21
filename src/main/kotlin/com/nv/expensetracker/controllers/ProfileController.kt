package com.nv.expensetracker.controllers

import com.nv.expensetracker.controllers.dto.ProfileResponse
import com.nv.expensetracker.database.model.Expense
import com.nv.expensetracker.database.repository.ExpenseRepository
import com.nv.expensetracker.database.repository.RefreshTokenRepository
import com.nv.expensetracker.database.repository.SavingGoalRepository
import com.nv.expensetracker.database.repository.UserRepository
import com.nv.expensetracker.controllers.enums.ExpenseCategory
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/profile")
class ProfileController(
    private val userRepository: UserRepository,
    private val expenseRepository: ExpenseRepository,
    private val savingGoalRepository: SavingGoalRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
) {

    @GetMapping
    fun getProfile(): ProfileResponse {
        val userId = ObjectId(SecurityContextHolder.getContext().authentication.principal as String)
        val user = userRepository.findById(userId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        }
        val expenses = expenseRepository.findByOwnerId(userId)
        val userType = determineUserType(expenses)
        val achievements = determineAchievements(userId, expenses)
        return ProfileResponse(
            email = user.email,
            userType = userType,
            achievements = achievements,
        )
    }

    @DeleteMapping
    fun deleteAccount() {
        val userId = ObjectId(SecurityContextHolder.getContext().authentication.principal as String)
        userRepository.deleteById(userId)
        val expenses = expenseRepository.findByOwnerId(userId)
        expenseRepository.deleteAll(expenses)
        savingGoalRepository.findByOwnerId(userId)?.let { savingGoalRepository.delete(it) }
        refreshTokenRepository.deleteAllByUserId(userId)
    }

    private fun determineUserType(expenses: List<Expense>): String {
        if (expenses.isEmpty()) return "Newcomer"
        val totals = expenses.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
        val topCategory = totals.maxByOrNull { it.value }?.key ?: return "Newcomer"
        return when (topCategory) {
            ExpenseCategory.TRAVEL -> "Traveler"
            ExpenseCategory.FOOD_AND_DRINK -> "Foodie"
            ExpenseCategory.ENTERTAINMENT -> "Entertainment Lover"
            ExpenseCategory.ESSENTIALS -> "Practical Spender"
            ExpenseCategory.HEALTH -> "Health Conscious"
            ExpenseCategory.EDUCATION -> "Learner"
            ExpenseCategory.HOME -> "Home Improver"
            ExpenseCategory.PERSONAL -> "Self-Care Enthusiast"
            ExpenseCategory.MISC -> "All-Rounder"
        }
    }

    private fun determineAchievements(userId: ObjectId, expenses: List<Expense>): List<String> {
        val achievements = mutableListOf<String>()
        if (expenses.isNotEmpty()) achievements += "First Expense Added"
        if (expenses.sumOf { it.amount } > 1000) achievements += "Spent over 1000"
        if (expenses.any { it.isRecurring }) achievements += "Added Recurring Expense"
        if (savingGoalRepository.findByOwnerId(userId) != null) achievements += "Saving Goal Set"
        return achievements
    }
}

