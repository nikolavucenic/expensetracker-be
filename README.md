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
