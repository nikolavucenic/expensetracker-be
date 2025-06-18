package com.nv.expensetracker.controllers.enums

enum class ExpenseType(val category: ExpenseCategory) {
    RENT(ExpenseCategory.ESSENTIALS),
    GROCERIES(ExpenseCategory.ESSENTIALS),
    UTILITIES(ExpenseCategory.ESSENTIALS),
    TRANSPORTATION(ExpenseCategory.ESSENTIALS),
    INSURANCE(ExpenseCategory.ESSENTIALS),
    LOAN_PAYMENT(ExpenseCategory.ESSENTIALS),
    PHONE(ExpenseCategory.ESSENTIALS),
    INTERNET(ExpenseCategory.ESSENTIALS),
    WATER(ExpenseCategory.ESSENTIALS),
    ELECTRICITY(ExpenseCategory.ESSENTIALS),
    GAS(ExpenseCategory.ESSENTIALS),

    FOOD(ExpenseCategory.FOOD_AND_DRINK),
    DINING_OUT(ExpenseCategory.FOOD_AND_DRINK),
    COFFEE(ExpenseCategory.FOOD_AND_DRINK),
    TAKEOUT(ExpenseCategory.FOOD_AND_DRINK),
    ALCOHOL(ExpenseCategory.FOOD_AND_DRINK),

    TRAVEL(ExpenseCategory.TRAVEL),
    FUEL(ExpenseCategory.TRAVEL),
    PUBLIC_TRANSPORT(ExpenseCategory.TRAVEL),
    ACCOMMODATION(ExpenseCategory.TRAVEL),
    FLIGHT(ExpenseCategory.TRAVEL),
    TAXI(ExpenseCategory.TRAVEL),

    HEALTHCARE(ExpenseCategory.HEALTH),
    MEDICINE(ExpenseCategory.HEALTH),
    GYM_MEMBERSHIP(ExpenseCategory.HEALTH),
    THERAPY(ExpenseCategory.HEALTH),

    EDUCATION(ExpenseCategory.EDUCATION),
    TUITION(ExpenseCategory.EDUCATION),
    BOOKS(ExpenseCategory.EDUCATION),
    COURSES(ExpenseCategory.EDUCATION),

    HOME_IMPROVEMENT(ExpenseCategory.HOME),
    MAINTENANCE(ExpenseCategory.HOME),
    FURNITURE(ExpenseCategory.HOME),
    APPLIANCES(ExpenseCategory.HOME),

    PERSONAL_CARE(ExpenseCategory.PERSONAL),
    CLOTHING(ExpenseCategory.PERSONAL),
    BEAUTY(ExpenseCategory.PERSONAL),
    HAIRCUT(ExpenseCategory.PERSONAL),

    ENTERTAINMENT(ExpenseCategory.ENTERTAINMENT),
    SUBSCRIPTIONS(ExpenseCategory.ENTERTAINMENT),
    MOVIES(ExpenseCategory.ENTERTAINMENT),
    GAMES(ExpenseCategory.ENTERTAINMENT),
    EVENTS(ExpenseCategory.ENTERTAINMENT),
    HOBBIES(ExpenseCategory.ENTERTAINMENT),

    GIFTS(ExpenseCategory.MISC),
    DONATIONS(ExpenseCategory.MISC),
    PETS(ExpenseCategory.MISC),
    CHILDCARE(ExpenseCategory.MISC),
    SAVINGS(ExpenseCategory.MISC),
    INVESTMENTS(ExpenseCategory.MISC),
    TAXES(ExpenseCategory.MISC),
    FINES(ExpenseCategory.MISC),
    FEES(ExpenseCategory.MISC),
    BUSINESS(ExpenseCategory.MISC),
    OTHER(ExpenseCategory.MISC),

    UNKNOWN(ExpenseCategory.MISC);
}