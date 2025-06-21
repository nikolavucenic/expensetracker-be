package com.nv.expensetracker.database.repository

import com.nv.expensetracker.database.model.Expense
import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

interface ExpenseRepositoryCustom {
    fun search(ownerId: ObjectId, filter: ExpenseFilter, sort: Sort, pageable: Pageable? = null): List<Expense>
}
