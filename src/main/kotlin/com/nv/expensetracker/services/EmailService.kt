package com.nv.expensetracker.services

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

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

        val payload = buildSendGridPayload(recipient, sender, emailBody)
        val url = URL("https://api.sendgrid.com/v3/mail/send")

        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            instanceFollowRedirects = false
            setRequestProperty("Authorization", "Bearer $apiKey")
            setRequestProperty("Content-Type", "application/json")
            doOutput = true
        }

        connection.outputStream.use { stream ->
            stream.write(payload.toByteArray(StandardCharsets.UTF_8))
        }

        val status = connection.responseCode
        if (status !in 200..299) {
            val errorBody = connection.errorStream?.bufferedReader()?.readText()
            logger.error(
                "Failed to send password reset email to {} with status {} and body {}",
                recipient,
                status,
                errorBody
            )
        } else {
            logger.info(
                "Password reset email sent to {} using {} via SendGrid with status {}",
                recipient,
                sender,
                status
            )
        }
    }

    private fun buildSendGridPayload(recipient: String, sender: String, emailBody: String): String = """
        {
          "personalizations": [
            {
              "to": [
                { "email": "$recipient" }
              ]
            }
          ],
          "from": { "email": "$sender" },
          "subject": "Your Expense Tracker password reset code",
          "content": [
            { "type": "text/plain", "value": ${jsonEscape(emailBody)} }
          ]
        }
    """.trimIndent()

    private fun jsonEscape(value: String): String {
        val escaped = value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
        return "\"$escaped\""
    }

    private fun resolveFromAddress(): String {
        val configured = fromAddress.trim()
        if (configured.isNotEmpty()) return configured

        logger.warn("mail.from is blank; defaulting to budgeter.rs@gmail.com")
        return "budgeter.rs@gmail.com"
    }
}
