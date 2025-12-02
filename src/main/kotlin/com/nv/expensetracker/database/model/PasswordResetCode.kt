package com.nv.expensetracker.database.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("password_reset_codes")
data class PasswordResetCode(
    @Id val id: ObjectId = ObjectId(),
    val userId: ObjectId,
    val hashedCode: String,
    @Indexed(expireAfter = "0s")
    val expiresAt: Instant,
)
