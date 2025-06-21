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
