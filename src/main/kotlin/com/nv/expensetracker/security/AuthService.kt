package com.nv.expensetracker.security

import com.nv.expensetracker.database.model.PasswordResetCode
import com.nv.expensetracker.database.model.RefreshToken
import com.nv.expensetracker.database.model.User
import com.nv.expensetracker.database.repository.PasswordResetCodeRepository
import com.nv.expensetracker.database.repository.RefreshTokenRepository
import com.nv.expensetracker.database.repository.UserRepository
import com.nv.expensetracker.services.EmailService
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import org.slf4j.LoggerFactory
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@Service
class AuthService(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val hashEncoder: HashEncoder,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val passwordResetCodeRepository: PasswordResetCodeRepository,
    private val emailService: EmailService,
) {

    private val logger = LoggerFactory.getLogger(AuthService::class.java)

    fun register(email: String, password: String): TokenPair {
        val existing = userRepository.findByEmail(email.trim())
        if (existing != null)
            throw ResponseStatusException(HttpStatus.CONFLICT, "A user with that email already exists.")

        val user = userRepository.save(
            User(
                email = email,
                hashedPassword = hashEncoder.encode(password)
            )
        )

        val accessToken = jwtService.generateAccessToken(user.id.toHexString())
        val refreshToken = jwtService.generateRefreshToken(user.id.toHexString())

        storeRefreshToken(user.id, refreshToken)

        return TokenPair(
            accessToken = accessToken,
            refreshToken = refreshToken,
        )
    }

    fun login(email: String, password: String): TokenPair {
        val user = userRepository.findByEmail(email) ?: throw BadCredentialsException("Invalid credentials.")

        if (!hashEncoder.matches(password, user.hashedPassword)) {
            throw BadCredentialsException("Invalid credentials.")
        }

        val newAccessToken = jwtService.generateAccessToken(user.id.toHexString())
        val newRefreshToken = jwtService.generateRefreshToken(user.id.toHexString())

        storeRefreshToken(user.id, newRefreshToken)

        return TokenPair(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }

    @Transactional
    fun refresh(refreshToken: String): TokenPair {
        if (!jwtService.validateRefreshToken(refreshToken))
            throw ResponseStatusException(HttpStatusCode.valueOf(401), "Invalid refresh token.")

        val userId = jwtService.getUserIdFromToken(refreshToken)
        val user = userRepository.findById(ObjectId(userId)).orElseThrow {
            ResponseStatusException(HttpStatusCode.valueOf(404), "Not found.")
        }

        val hashed = hashToken(refreshToken)
        refreshTokenRepository.findByUserIdAndHashedToken(user.id, hashed)
            ?: throw ResponseStatusException(HttpStatusCode.valueOf(401), "Refresh token not recognized (maybe used or expired)")

        refreshTokenRepository.deleteByUserIdAndHashedToken(user.id, hashed)

        val newAccessToken = jwtService.generateAccessToken(userId)
        val newRefreshToken = jwtService.generateRefreshToken(userId)

        storeRefreshToken(user.id, newRefreshToken)

        return TokenPair(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken,
        )
    }

    private fun storeRefreshToken(userId: ObjectId, rawRefreshToken: String) {
        val hashed = hashToken(rawRefreshToken)
        val expiryMs = jwtService.refreshTokenValidityMs
        val expiresAt = Instant.now().plusMillis(expiryMs)

        refreshTokenRepository.save(
            RefreshToken(
                userId = userId,
                expiresAt = expiresAt,
                hashedToken = hashed
            )
        )
    }

    fun logout(refreshToken: String) {
        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw ResponseStatusException(HttpStatusCode.valueOf(401), "Invalid refresh token.")
        }
        val userId = jwtService.getUserIdFromToken(refreshToken)
        val hashed = hashToken(refreshToken)
        refreshTokenRepository.deleteByUserIdAndHashedToken(ObjectId(userId), hashed)
    }

    fun requestPasswordReset(email: String) {
        val user = userRepository.findByEmail(email.trim()) ?: return

        passwordResetCodeRepository.deleteAllByUserId(user.id)

        val (code, hashed) = generateUniqueResetCode()
        val expiry = Instant.now().plusMillis(RESET_CODE_VALIDITY_MS)

        passwordResetCodeRepository.save(
            PasswordResetCode(
                userId = user.id,
                hashedCode = hashed,
                expiresAt = expiry,
            )
        )

        try {
            CompletableFuture.runAsync { emailService.sendResetCode(user.email, code) }
                .orTimeout(5, TimeUnit.SECONDS)
                .join()
            logger.info("Password reset code sent to ${'$'}{user.email}")
        } catch (ex: Exception) {
            logger.error("Failed to send password reset code to ${'$'}{user.email}", ex)
            throw ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Unable to send a reset code right now (email delivery failed: ${'$'}{ex.message ?: ex.javaClass.simpleName}). Please try again later.",
                ex
            )
        }
    }

    fun verifyResetCode(code: String): String {
        val hashedCode = hashResetCode(code)
        val resetEntry = passwordResetCodeRepository.findByHashedCode(hashedCode)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid reset code.")

        if (resetEntry.expiresAt.isBefore(Instant.now())) {
            passwordResetCodeRepository.deleteById(resetEntry.id)
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid reset code.")
        }

        passwordResetCodeRepository.deleteById(resetEntry.id)

        return jwtService.generateResetSessionToken(resetEntry.userId.toHexString())
    }

    fun resetPassword(resetSessionToken: String, newPassword: String) {
        if (!jwtService.validateResetSessionToken(resetSessionToken)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired reset session token.")
        }

        val userId = ObjectId(jwtService.getUserIdFromToken(resetSessionToken))
        val user = userRepository.findById(userId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.")
        }

        userRepository.save(user.copy(hashedPassword = hashEncoder.encode(newPassword)))
        refreshTokenRepository.deleteAllByUserId(user.id)
        passwordResetCodeRepository.deleteAllByUserId(user.id)
    }

    private fun hashToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }

    private fun hashResetCode(code: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(code.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }

    private fun generateUniqueResetCode(): Pair<String, String> {
        var code: String
        var hashed: String
        do {
            code = Random.nextInt(0, 1_000_000).toString().padStart(6, '0')
            hashed = hashResetCode(code)
        } while (passwordResetCodeRepository.findByHashedCode(hashed) != null)

        return code to hashed
    }

    companion object {
        private const val RESET_CODE_VALIDITY_MS = 10 * 60 * 1000L
    }

}