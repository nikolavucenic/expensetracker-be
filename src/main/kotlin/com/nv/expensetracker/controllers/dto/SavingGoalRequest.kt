package com.nv.expensetracker.controllers.dto

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import java.time.Instant

data class SavingGoalRequest(
    val id: String?,
    @field:NotBlank(message = "Goal name can't be empty.")
    val name: String,
    @field:Min(value = 1, message = "Target amount must be positive.")
    val targetAmount: Int,
    @field:JsonDeserialize(using = InstantEpochMillisDeserializer::class)
    val targetDate: Instant,
)
