package com.nv.expensetracker.services

import com.sendgrid.Method
import com.sendgrid.Request
import com.sendgrid.SendGrid
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.Content
import com.sendgrid.helpers.mail.objects.Email
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class EmailService(
    @Value("\${sendgrid.api-key:}") private val apiKey: String,
    @Value("\${mail.from:budgeter.rs@gmail.com}") private val fromAddress: String,
) {

    private val logger = LoggerFactory.getLogger(EmailService::class.java)

    @Async
    fun sendResetCodeAsync(recipient: String, code: String) {
        try {
            sendResetCode(recipient, code)
        } catch (ex: Exception) {
            logger.error("Async password reset email failed for {}, ${ex.message}", recipient, ex)
        }
    }

    private fun sendResetCode(recipient: String, code: String) {
        if (apiKey.isBlank()) {
            logger.error("SendGrid API key is not configured; cannot send reset email to {}", recipient)
            return
        }

        val sender = resolveFromAddress()
        val emailBody = """
            Use the verification code below to reset your password:

            $code

            This code will expire in 10 minutes.
        """.trimIndent()

        val mail = Mail(
            Email(sender),
            "Your Expense Tracker password reset code",
            Email(recipient),
            Content("text/plain", emailBody)
        )

        val request = Request().apply {
            method = Method.POST
            endpoint = "mail/send"
            body = mail.build()
        }

        val response = SendGrid(apiKey).api(request)
        if (response.statusCode !in 200..299) {
            logger.error(
                "Failed to send password reset email to {} with status {} and body {}",
                recipient,
                response.statusCode,
                response.body
            )
        } else {
            logger.info(
                "Password reset email sent to {} using {} via SendGrid with status {}",
                recipient,
                sender,
                response.statusCode
            )
        }
    }

    private fun resolveFromAddress(): String {
        val configured = fromAddress.trim()
        if (configured.isNotEmpty()) return configured

        logger.warn("mail.from is blank; defaulting to budgeter.rs@gmail.com")
        return "budgeter.rs@gmail.com"
    }
}
