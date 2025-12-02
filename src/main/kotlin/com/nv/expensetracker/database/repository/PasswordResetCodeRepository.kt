package com.nv.expensetracker.database.repository

import com.nv.expensetracker.database.model.PasswordResetCode
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface PasswordResetCodeRepository : MongoRepository<PasswordResetCode, ObjectId> {

    fun findByHashedCode(hashedCode: String): PasswordResetCode?

    fun deleteAllByUserId(userId: ObjectId)
}
