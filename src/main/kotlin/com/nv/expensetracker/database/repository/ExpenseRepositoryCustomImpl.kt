package com.nv.expensetracker.database.repository

import com.nv.expensetracker.database.model.Expense
import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class ExpenseRepositoryCustomImpl(
    private val mongoTemplate: MongoTemplate
) : ExpenseRepositoryCustom {

    override fun search(ownerId: ObjectId, filter: ExpenseFilter, sort: Sort, pageable: Pageable?): List<Expense> {
        val criteriaList = mutableListOf<Criteria>()
        criteriaList += Criteria.where("ownerId").`is`(ownerId)
        filter.type?.let { criteriaList += Criteria.where("type").`is`(it) }
        filter.date?.let { criteriaList += Criteria.where("date").`is`(it) }
        if (filter.dateFrom != null || filter.dateTo != null) {
            val dateCriteria = Criteria("date")
            filter.dateFrom?.let { dateCriteria.gte(it) }
            filter.dateTo?.let { dateCriteria.lte(it) }
            criteriaList += dateCriteria
        }
        filter.amount?.let { criteriaList += Criteria.where("amount").`is`(it) }
        if (filter.amountFrom != null || filter.amountTo != null) {
            val amountCriteria = Criteria("amount")
            filter.amountFrom?.let { amountCriteria.gte(it) }
            filter.amountTo?.let { amountCriteria.lte(it) }
            criteriaList += amountCriteria
        }
        filter.isRecurring?.let { criteriaList += Criteria.where("isRecurring").`is`(it) }
        filter.search?.let { search ->
            criteriaList += Criteria().orOperator(
                Criteria.where("name").regex(search, "i"),
                Criteria.where("description").regex(search, "i")
            )
        }

        val criteria = Criteria().andOperator(*criteriaList.toTypedArray())
        val query = Query(criteria).with(sort)
        pageable?.let { query.with(it) }

        return mongoTemplate.find(query, Expense::class.java)
    }
}
