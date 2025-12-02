package com.nv.expensetracker.controllers.dto

import java.time.Instant

enum class BannerType {
    UPCOMING_EXPENSE,
    SAVING_GOAL,
    WELCOME,
}

data class UpcomingExpenseBanner(
    val id: String,
    val name: String,
    val description: String?,
    val amount: Int,
    val date: Instant,
)

data class SavingGoalBanner(
    val id: String,
    val name: String,
    val savedAmount: Int,
    val targetAmount: Int,
    val progress: Double,
    val targetDate: Instant,
)

data class BannerResponse(
    val type: BannerType,
    val upcomingExpense: UpcomingExpenseBanner? = null,
    val savingGoal: SavingGoalBanner? = null,
    val welcomeMessage: String? = null,
)
