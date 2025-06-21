package com.nv.expensetracker.database.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("saving_goals")
data class SavingGoal(
    @Id val id: ObjectId = ObjectId.get(),
    val ownerId: ObjectId,
    val name: String,
    val targetAmount: Int,
    val targetDate: Instant,
    val createdAt: Instant = Instant.now(),
)
