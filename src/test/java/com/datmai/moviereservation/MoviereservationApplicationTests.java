package com.datmai.moviereservation;

import com.datmai.moviereservation.controller.AuthController;
import com.datmai.moviereservation.controller.EmailController;
import com.datmai.moviereservation.controller.UserController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MoviereservationApplicationTests {

	@InjectMocks
	private UserController userController;

	@InjectMocks
	private AuthController authController;

	@InjectMocks
	private EmailController emailController;

	@Test
	void contextLoads() {
		Assertions.assertNotNull(userController);
		Assertions.assertNotNull(authController);
		Assertions.assertNotNull(emailController);
	}

}
