package com.nv.expensetracker.database.repository

import com.nv.expensetracker.database.model.Expense
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface ExpenseRepository : MongoRepository<Expense, ObjectId> {

    fun findByOwnerId(ownerId: ObjectId): List<Expense>

}