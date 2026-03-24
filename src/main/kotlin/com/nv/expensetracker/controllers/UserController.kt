package com.nv.expensetracker.controllers

import com.nv.expensetracker.controllers.dto.UserResponse
import com.nv.expensetracker.database.repository.UserRepository
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/user")
class UserController(
    private val userRepository: UserRepository,
) {

    @GetMapping
    fun getUser(): UserResponse {
        val userId = ObjectId(SecurityContextHolder.getContext().authentication.principal as String)
        val user = userRepository.findById(userId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        }

        return UserResponse(
            name = user.name,
            email = user.email,
        )
    }
}
