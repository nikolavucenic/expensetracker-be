package com.nv.expensetracker.security

data class TokenPair(
    val accessToken: String,
    val refreshToken: String,
)
