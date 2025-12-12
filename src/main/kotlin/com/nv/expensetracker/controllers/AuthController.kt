package com.nv.expensetracker.controllers

import com.nv.expensetracker.security.AuthService
import com.nv.expensetracker.security.TokenPair
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/auth"])
class AuthController(
    private val authService: AuthService
) {

    data class AuthRequest(
        @field:Email(message = "Invalid email format.")
        val email: String,
        @field:Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{9,}\$",
            message = "Password must be at least 9 characters long and contain at least one digit, an uppercase letter, and a lowercase letter."
        )
        val password: String,
    )

    data class RefreshRequest(
        val refreshToken: String
    )

    data class RequestPasswordResetRequest(
        @field:Email(message = "Invalid email format.")
        val email: String,
    )

    data class VerifyResetCodeRequest(
        @field:NotBlank(message = "Verification code is required.")
        val code: String,
    )

    data class ResetPasswordRequest(
        @field:NotBlank(message = "Reset session token is required.")
        val resetSessionToken: String,
        @field:Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{9,}\$",
            message = "Password must be at least 9 characters long and contain at least one digit, an uppercase letter, and a lowercase letter."
        )
        val newPassword: String,
    )

    data class ResetSessionResponse(
        val resetSessionToken: String,
    )

    @PostMapping(path = ["/register"])
    fun register(
        @Valid @RequestBody body: AuthRequest
    ): TokenPair =
        authService.register(body.email, body.password)

    @PostMapping(path = ["/login"])
    fun login(
        @RequestBody body: AuthRequest
    ): TokenPair =
        authService.login(body.email, body.password)

    @PostMapping(path = ["/refresh"])
    fun refresh(
        @RequestBody body: RefreshRequest
    ): TokenPair =
        authService.refresh(body.refreshToken)

    @PostMapping(path = ["/logout"])
    fun logout(
        @RequestBody body: RefreshRequest
    ) {
        authService.logout(body.refreshToken)
    }

    @PostMapping(path = ["/request-password-reset"])
    fun requestPasswordReset(
        @Valid @RequestBody body: RequestPasswordResetRequest
    ) : ResponseEntity<Void> {
        authService.requestPasswordReset(body.email)
        return ResponseEntity.ok().build()
    }

    @PostMapping(path = ["/verify-reset-code"])
    fun verifyResetCode(
        @Valid @RequestBody body: VerifyResetCodeRequest
    ): ResetSessionResponse =
        ResetSessionResponse(authService.verifyResetCode(body.code))

    @PostMapping(path = ["/reset-password"])
    fun resetPassword(
        @Valid @RequestBody body: ResetPasswordRequest
    ) {
        authService.resetPassword(body.resetSessionToken, body.newPassword)
    }

}