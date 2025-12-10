package com.nv.expensetracker.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val mailSender: JavaMailSender,
    @Value("\${mail.from:no-reply@budgeter.rs}") private val fromAddress: String,
) {

    fun sendResetCode(recipient: String, code: String) {
        val message = SimpleMailMessage().apply {
            setTo(recipient)
            from = fromAddress
            subject = "Your Expense Tracker password reset code"
            text = "Use the verification code below to reset your password:\n\n${code}\n\nThis code will expire in 10 minutes."
        }

        mailSender.send(message)
    }
}
