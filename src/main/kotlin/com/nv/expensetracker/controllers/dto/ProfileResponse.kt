package com.nv.expensetracker.controllers.dto

data class ProfileResponse(
    val email: String,
    val userType: String,
    val achievements: List<String>,
)
