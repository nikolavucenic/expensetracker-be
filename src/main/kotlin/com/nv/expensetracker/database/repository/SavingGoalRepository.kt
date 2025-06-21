package com.nv.expensetracker.database.repository

import com.nv.expensetracker.database.model.SavingGoal
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface SavingGoalRepository : MongoRepository<SavingGoal, ObjectId> {
    fun findByOwnerId(ownerId: ObjectId): SavingGoal?
}
