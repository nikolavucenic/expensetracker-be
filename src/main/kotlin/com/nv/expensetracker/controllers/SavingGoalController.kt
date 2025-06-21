package com.nv.expensetracker.controllers

import com.nv.expensetracker.controllers.dto.SavingGoalRequest
import com.nv.expensetracker.controllers.dto.SavingGoalResponse
import com.nv.expensetracker.controllers.enums.ExpenseType
import com.nv.expensetracker.database.model.SavingGoal
import com.nv.expensetracker.database.repository.ExpenseFilter
import com.nv.expensetracker.database.repository.ExpenseRepository
import com.nv.expensetracker.database.repository.SavingGoalRepository
import jakarta.validation.Valid
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.Duration
import java.time.Instant
import kotlin.math.ceil

@RestController
@RequestMapping("/saving-goal")
class SavingGoalController(
    private val repository: SavingGoalRepository,
    private val expenseRepository: ExpenseRepository,
) {

    @GetMapping
    fun getGoal(): SavingGoalResponse? {
        val ownerId = ObjectId(SecurityContextHolder.getContext().authentication.principal as String)
        val goal = repository.findByOwnerId(ownerId) ?: return null
        return goal.toResponse(ownerId)
    }

    @PostMapping
    fun createGoal(
        @Valid @RequestBody body: SavingGoalRequest
    ): SavingGoalResponse {
        val ownerId = ObjectId(SecurityContextHolder.getContext().authentication.principal as String)
        if (repository.findByOwnerId(ownerId) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Saving goal already exists")
        }
        val goal = repository.save(
            SavingGoal(
                id = body.id?.let { ObjectId(it) } ?: ObjectId.get(),
                ownerId = ownerId,
                name = body.name,
                targetAmount = body.targetAmount,
                targetDate = body.targetDate,
            )
        )
        return goal.toResponse(ownerId)
    }

    @PutMapping(path = ["/{id}"])
    fun updateGoal(
        @PathVariable id: String,
        @Valid @RequestBody body: SavingGoalRequest
    ): SavingGoalResponse {
        val ownerId = ObjectId(SecurityContextHolder.getContext().authentication.principal as String)
        val goal = repository.findById(ObjectId(id)).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Goal not found")
        }
        if (goal.ownerId != ownerId) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Goal not found")
        }
        val updated = repository.save(
            goal.copy(
                name = body.name,
                targetAmount = body.targetAmount,
                targetDate = body.targetDate,
            )
        )
        return updated.toResponse(ownerId)
    }

    @DeleteMapping(path = ["/{id}"])
    fun deleteGoal(@PathVariable id: String) {
        val ownerId = ObjectId(SecurityContextHolder.getContext().authentication.principal as String)
        val goal = repository.findById(ObjectId(id)).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Goal not found")
        }
        if (goal.ownerId != ownerId) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Goal not found")
        }
        repository.delete(goal)
    }

    private fun SavingGoal.toResponse(ownerId: ObjectId): SavingGoalResponse {
        val expenses = expenseRepository.search(
            ownerId,
            ExpenseFilter(type = ExpenseType.SAVINGS),
            org.springframework.data.domain.Sort.unsorted(),
            null
        )
        val saved = expenses.sumOf { it.amount }
        val progress = if (targetAmount == 0) 0.0 else saved.toDouble() / targetAmount
        val monthly = calculateMonthlyRequired(saved)
        return SavingGoalResponse(
            id = id.toHexString(),
            name = name,
            targetAmount = targetAmount,
            targetDate = targetDate,
            savedAmount = saved,
            progress = progress,
            monthlyRequired = monthly,
        )
    }

    private fun SavingGoal.calculateMonthlyRequired(saved: Int): Int {
        val remaining = targetAmount - saved
        if (remaining <= 0) return 0
        val now = Instant.now()
        if (!targetDate.isAfter(now)) return remaining
        val daysLeft = Duration.between(now, targetDate).toDays()
        val monthsLeft = ceil(daysLeft / 30.0).toLong()
        return if (monthsLeft <= 0) remaining else ceil(remaining.toDouble() / monthsLeft).toInt()
    }
}
