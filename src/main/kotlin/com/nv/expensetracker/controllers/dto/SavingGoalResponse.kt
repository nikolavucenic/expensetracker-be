package com.nv.expensetracker.controllers.dto

import java.time.Instant

data class SavingGoalResponse(
    val id: String,
    val name: String,
    val targetAmount: Int,
    val targetDate: Instant,
    val savedAmount: Int,
    val progress: Double,
    val monthlyRequired: Int,
)
