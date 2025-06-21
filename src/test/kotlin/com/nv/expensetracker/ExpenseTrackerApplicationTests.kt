package com.nv.expensetracker

import com.nv.expensetracker.security.AuthService
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ExpenseTrackerApplicationTests {

	@Autowired
	lateinit var authService: AuthService

	@Test
	fun contextLoads() {
		assertNotNull(authService)
	}

}
