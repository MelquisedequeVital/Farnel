package br.edu.ifpb.gps.farnel;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class FarnelApplicationTests {

	@Test
	void contextLoads() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		assertTrue(encoder.matches("123456", "$2a$10$.39kV.D/Et3eHQ/akbckVewJg/wVfP3vwqxj/YqtZwINvuzHSTD2C"));
	}

}
