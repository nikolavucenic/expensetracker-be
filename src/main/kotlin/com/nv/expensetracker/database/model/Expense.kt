package com.nv.expensetracker.database.model

import com.nv.expensetracker.controllers.enums.ExpenseCategory
import com.nv.expensetracker.controllers.enums.ExpenseType
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("expenses")
data class Expense(
    @Id val id: ObjectId = ObjectId.get(),
    val ownerId: ObjectId,
    val name: String,
    val description: String? = null,
    val amount: Int,
    val type: ExpenseType,
    val category: ExpenseCategory,
    val date: Instant = Instant.now(),
    val isRecurring: Boolean = false,
)
