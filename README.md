# Expense Tracker Backend

## `/expenses` GET Query Parameters

The `GET /expenses` endpoint now supports filtering, sorting and pagination.

| Parameter      | Description                                                     |
|---------------|-----------------------------------------------------------------|
| `sort`        | One of `date_desc` (default), `date_asc`, `amount_desc`, `amount_asc` |
| `type`        | Filter by `ExpenseType`                                          |
| `date`        | Exact date (ISO-8601)                                            |
| `dateFrom`    | Start of date range (ISO-8601)                                   |
| `dateTo`      | End of date range (ISO-8601)                                     |
| `amount`      | Exact amount                                                     |
| `amountFrom`  | Minimum amount                                                   |
| `amountTo`    | Maximum amount                                                   |
| `isRecurring` | Filter recurring expenses                                        |
| `search`      | Case-insensitive search in name or description                   |
| `page`        | Page number (0 based)                                            |
| `size`        | Page size                                                        |

If `page` and `size` are provided the result is paginated; otherwise all
matching expenses are returned.

## Saving Goal Endpoints

### `GET /saving-goal`
Returns the current saving goal for the authenticated user. If no goal is set the response is `null`.

### `POST /saving-goal`
Creates a new saving goal. Only one goal per user is allowed.
Request body:
```json
{
  "name": "Vacation",
  "targetAmount": 1000,
  "targetDate": "2025-01-01T00:00:00Z"
}
```

### `PUT /saving-goal/{id}`
Updates an existing goal with the provided data.

### `DELETE /saving-goal/{id}`
Deletes the user's saving goal.

The response object for creating or fetching the goal contains the amount already saved (based on expenses of type `SAVINGS`), progress ratio and the estimated monthly amount required to reach the goal by the target date.

## Profile Endpoints

### `GET /profile`
Returns the authenticated user's profile information.

### `DELETE /profile`
Deletes the user account along with all associated data.

### `POST /auth/logout`
Invalidates the provided refresh token.

### Password reset (two-step)

1. `POST /auth/request-password-reset` with the account email. If the account exists, a six-digit code is generated, persisted, and emailed to the user. The response is always empty to avoid leaking which emails are registered.
2. `POST /auth/verify-reset-code` with the received code. A short-lived reset session token is returned if the code is valid.
3. `POST /auth/reset-password` with the reset session token and a new password that matches the existing password rules.

On successful password reset all existing refresh tokens are revoked.

## Deployment configuration

When deploying (for example on Railway), set the following environment variables so the application can start and send email:

| Variable | Purpose |
| --- | --- |
| `PORT` | Port the application should bind to (Railway typically injects this automatically). |
| `MONGODB_CONNECTION_STRING` | Connection string for the MongoDB instance. |
| `JWT_SECRET_BASE64` | Base64-encoded secret key for JWT signing. |
| `SMTP_HOST` | SMTP server host for sending password reset emails. |
| `SMTP_PORT` | SMTP server port (defaults to `587` if omitted). |
| `SMTP_USERNAME` | SMTP username. |
| `SMTP_PASSWORD` | SMTP password. |
| `MAIL_FROM` | Sender address for outgoing email (defaults to `no-reply@budgeter.rs` if omitted). |

On Railway, add these as project environment variables; the Spring configuration reads them automatically via `application.properties`.

### Gmail SMTP example

If you use Gmail/Google Workspace as the SMTP provider:

- `SMTP_HOST=smtp.gmail.com`
- `SMTP_PORT=587`
- `SMTP_USERNAME=<your full Gmail/Workspace email address>` (e.g., `me@budgeter.rs`)
- `SMTP_PASSWORD=<Gmail App Password>` — generate a 16-character app password in your Google account security settings; standard login passwords will be blocked by Gmail for SMTP.
- `MAIL_FROM=<the address you set above>` (e.g., `no-reply@budgeter.rs`)

Ensure 2-Step Verification is enabled on the account before creating the app password; Gmail will not accept regular passwords for SMTP.
