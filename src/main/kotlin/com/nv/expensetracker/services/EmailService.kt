package com.nv.expensetracker.services

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val mailSender: JavaMailSender,
    @Value("\${mail.from:no-reply@budgeter.rs}") private val fromAddress: String,
) {

    private val logger = LoggerFactory.getLogger(EmailService::class.java)

    fun sendResetCode(recipient: String, code: String) {
        val senderImpl = mailSender as? JavaMailSenderImpl
        val resolvedFrom = resolveFromAddress(senderImpl)

        val message = SimpleMailMessage().apply {
            setTo(recipient)
            from = resolvedFrom
            subject = "Your Expense Tracker password reset code"
            text = "Use the verification code below to reset your password:\n\n${code}\n\nThis code will expire in 10 minutes."
        }

        try {
            mailSender.send(message)
            logger.info(
                "Password reset email queued for {} using {} via {}",
                recipient,
                resolvedFrom,
                senderImpl?.host ?: "unknown host",
            )
        } catch (ex: Exception) {
            logger.error(
                "Failed to send password reset email to {} using {} via {}",
                recipient,
                resolvedFrom,
                senderImpl?.host ?: "unknown host",
                ex,
            )
            throw ex
        }
    }

    private fun resolveFromAddress(senderImpl: JavaMailSenderImpl?): String {
        val configured = fromAddress.trim()
        if (configured.isNotEmpty()) return configured

        val username = senderImpl?.username?.takeIf { it.isNotBlank() }
        if (username != null) return username

        logger.warn("mail.from is blank and SMTP username is not set; defaulting to no-reply@budgeter.rs")
        return "no-reply@budgeter.rs"
    }
}
