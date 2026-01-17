package com.nv.expensetracker.controllers.enums

enum class ExpenseType(val category: ExpenseCategory) {
    RENT(ExpenseCategory.ESSENTIALS),
    GROCERIES(ExpenseCategory.ESSENTIALS),
    UTILITIES(ExpenseCategory.ESSENTIALS),
    PHONE(ExpenseCategory.ESSENTIALS),
    INTERNET(ExpenseCategory.ESSENTIALS),
    INSURANCE(ExpenseCategory.ESSENTIALS),
    LOAN_PAYMENT(ExpenseCategory.ESSENTIALS),

    TAKEOUT(ExpenseCategory.FOOD_AND_DRINK),
    EATING_OUT(ExpenseCategory.FOOD_AND_DRINK),
    COFFEE(ExpenseCategory.FOOD_AND_DRINK),
    ALCOHOL(ExpenseCategory.FOOD_AND_DRINK),

    FUEL(ExpenseCategory.TRAVEL),
    PUBLIC_TRANSPORT(ExpenseCategory.TRAVEL),
    CAR_MAINTENANCE(ExpenseCategory.TRAVEL),
    CAR_INSURANCE(ExpenseCategory.TRAVEL),
    ACCOMMODATION(ExpenseCategory.TRAVEL),
    TOLLS(ExpenseCategory.TRAVEL),

    HEALTHCARE(ExpenseCategory.HEALTH),
    MEDICAL_VISITS(ExpenseCategory.HEALTH),
    DENTAL(ExpenseCategory.HEALTH),
    MEDICINE(ExpenseCategory.HEALTH),
    GYM_MEMBERSHIP(ExpenseCategory.HEALTH),
    THERAPY(ExpenseCategory.HEALTH),

    EDUCATION(ExpenseCategory.EDUCATION),
    BOOKS(ExpenseCategory.EDUCATION),
    COURSES(ExpenseCategory.EDUCATION),

    HOME_REPAIR(ExpenseCategory.HOME),
    FURNITURE(ExpenseCategory.HOME),
    APPLIANCES(ExpenseCategory.HOME),

    CLOTHING(ExpenseCategory.PERSONAL),
    BEAUTY(ExpenseCategory.PERSONAL),
    HAIRCUT(ExpenseCategory.PERSONAL),

    SUBSCRIPTIONS(ExpenseCategory.ENTERTAINMENT),
    GAMES(ExpenseCategory.ENTERTAINMENT),
    HOBBIES(ExpenseCategory.ENTERTAINMENT),
    SOFTWARE(ExpenseCategory.ENTERTAINMENT),
    CLOUD_SERVICES(ExpenseCategory.ENTERTAINMENT),

    GIFTS(ExpenseCategory.MISC),
    CHARITY(ExpenseCategory.MISC),
    PETS(ExpenseCategory.MISC),
    CHILD_EXPENSES(ExpenseCategory.MISC),
    FEES_AND_FINES(ExpenseCategory.MISC),
    EMERGENCY(ExpenseCategory.MISC),
    SAVINGS(ExpenseCategory.MISC),
    OTHER(ExpenseCategory.MISC),
    UNKNOWN(ExpenseCategory.MISC)
}