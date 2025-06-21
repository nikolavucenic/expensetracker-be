package com.nv.expensetracker.controllers

import com.nv.expensetracker.controllers.dto.ExpenseRequest
import com.nv.expensetracker.controllers.dto.ExpenseResponse
import com.nv.expensetracker.database.model.Expense
import com.nv.expensetracker.database.repository.ExpenseFilter
import com.nv.expensetracker.database.repository.ExpenseRepository
import com.nv.expensetracker.controllers.enums.ExpenseType
import jakarta.validation.Valid
import org.bson.types.ObjectId
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.time.Instant
import kotlin.collections.map
import kotlin.getOrThrow
import kotlin.let
import kotlin.mapCatching
import kotlin.runCatching

@RestController
@RequestMapping("/expenses")
class ExpenseController(
    private val repository: ExpenseRepository
) {

    @PostMapping
    fun save(
        @Valid @RequestBody body: ExpenseRequest
    ): ExpenseResponse =
        repository.runCatching {
            save(
                Expense(
                    name = body.name,
                    description = body.description,
                    amount = body.amount,
                    type = body.type,
                    category = body.type.category,
                    date = body.date,
                    isRecurring = body.isRecurring,
                    id = body.id?.let { ObjectId(it) } ?: ObjectId.get(),
                    ownerId = ObjectId(SecurityContextHolder.getContext().authentication.principal as String),
                )
            )
        }.mapCatching { expense ->
            expense.toResponse()
        }.getOrThrow()


    @GetMapping
    fun findByOwnerId(
        @RequestParam(required = false) sort: String?,
        @RequestParam(required = false) type: ExpenseType?,
        @RequestParam(required = false) date: Instant?,
        @RequestParam(required = false) dateFrom: Instant?,
        @RequestParam(required = false) dateTo: Instant?,
        @RequestParam(required = false) amount: Int?,
        @RequestParam(required = false) amountFrom: Int?,
        @RequestParam(required = false) amountTo: Int?,
        @RequestParam(required = false) isRecurring: Boolean?,
        @RequestParam(required = false) search: String?,
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) size: Int?,
    ): List<ExpenseResponse> {
        val ownerId = ObjectId(SecurityContextHolder.getContext().authentication.principal as String)

        val filter = ExpenseFilter(
            type = type,
            date = date,
            dateFrom = dateFrom,
            dateTo = dateTo,
            amount = amount,
            amountFrom = amountFrom,
            amountTo = amountTo,
            isRecurring = isRecurring,
            search = search,
        )

        val sortSpec = when (sort) {
            "date_asc" -> Sort.by(Sort.Direction.ASC, "date")
            "amount_desc" -> Sort.by(Sort.Direction.DESC, "amount")
            "amount_asc" -> Sort.by(Sort.Direction.ASC, "amount")
            else -> Sort.by(Sort.Direction.DESC, "date")
        }

        val pageable = if (page != null && size != null) PageRequest.of(page, size) else null

        val expenses = repository.search(ownerId, filter, sortSpec, pageable)
        return expenses.map { it.toResponse() }
    }

    @DeleteMapping(path = ["/{id}"])
    fun deleteById(@PathVariable id: String) {
        val expense = repository.findById(ObjectId(id)).orElseThrow {
            kotlin.IllegalArgumentException("Expense not found")
        }
        val ownerId = SecurityContextHolder.getContext().authentication.principal as String
        if (expense.ownerId.toHexString() == ownerId) {
            repository.deleteById(ObjectId(id ))
        }
    }

    private fun Expense.toResponse(): ExpenseResponse =
        ExpenseResponse(
            id = id.toHexString(),
            name = name,
            description = description,
            amount = amount,
            type = type,
            category = category,
            date = date,
            isRecurring = isRecurring
        )

}