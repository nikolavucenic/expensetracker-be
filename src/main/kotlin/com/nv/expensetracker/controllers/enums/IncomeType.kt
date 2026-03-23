package com.nv.expensetracker.controllers.enums

enum class IncomeType(val category: IncomeCategory) {
    SALARY(IncomeCategory.ACTIVE),
    FREELANCE(IncomeCategory.ACTIVE),
    BUSINESS(IncomeCategory.ACTIVE),
    BONUS(IncomeCategory.ACTIVE),
    OVERTIME(IncomeCategory.ACTIVE),

    SIDE_HUSTLE(IncomeCategory.SIDE),
    SELLING_ITEMS(IncomeCategory.SIDE),
    RESALE(IncomeCategory.SIDE),

    RENTAL_INCOME(IncomeCategory.PASSIVE),
    DIVIDENDS(IncomeCategory.PASSIVE),
    INTEREST(IncomeCategory.PASSIVE),

    GIFTS(IncomeCategory.OTHER),
    REFUNDS(IncomeCategory.OTHER),

    OTHER(IncomeCategory.OTHER),
    UNKNOWN(IncomeCategory.OTHER),
}
