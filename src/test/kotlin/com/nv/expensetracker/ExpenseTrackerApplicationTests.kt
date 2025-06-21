package com.nv.expensetracker

import com.nv.expensetracker.security.AuthService
import com.nv.expensetracker.database.repository.SavingGoalRepository
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ExpenseTrackerApplicationTests {

        @Autowired
        lateinit var authService: AuthService

        @Autowired
        lateinit var savingGoalRepository: SavingGoalRepository

	@Test
        fun contextLoads() {
                assertNotNull(authService)
                assertNotNull(savingGoalRepository)
        }

}
