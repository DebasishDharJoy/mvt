//package com.mvt.backApp;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest(properties = {
//		"spring.flyway.enabled=false",
//		"spring.jpa.hibernate.ddl-auto=none",
//		"app.bootstrap.enabled=false",
//		"app.jwt.secret=01234567890123456789012345678901",
//		"app.cors.allowed-origins=http://localhost:5173",
//		"app.storage.local.upload-dir=./target/test-uploads"
//})
//class BackAppApplicationTests {
//	@Test
//	void contextLoads() {
//	}
//}


package com.mvt.backApp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.mock;

@SpringBootTest(
		properties = {
				"spring.flyway.enabled=false",
				"spring.jpa.hibernate.ddl-auto=none",

				"app.bootstrap.enabled=false",

				"app.jwt.secret=01234567890123456789012345678901",
				"app.cors.allowed-origins=http://localhost:5173",

				"app.storage.provider=local",
				"app.storage.local.upload-dir=./target/test-uploads",

				"app.mail.accounts.otp.host=localhost",
				"app.mail.accounts.otp.port=1025",
				"app.mail.accounts.otp.username=test",
				"app.mail.accounts.otp.password=test",
				"app.mail.accounts.otp.from-address=test@example.invalid",
				"app.mail.accounts.otp.smtp-auth=false",
				"app.mail.accounts.otp.starttls=false"
		}
)
class BackAppApplicationTests {

	@Test
	void contextLoads() {
	}
}

//	@TestConfiguration(proxyBeanMethods = false)
//	static class TestMailConfiguration {
//
//		@Bean
//		JavaMailSender javaMailSender() {
//			return mock(JavaMailSender.class);
//		}
//	}
//}