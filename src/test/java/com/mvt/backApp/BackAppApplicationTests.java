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
				"app.jwt.expiration-ms=86400000",

				"app.cors.allowed-origins=http://localhost:5173",

				"app.storage.provider=local",
				"app.storage.local.upload-dir=./target/test-uploads",

				// ==================================================
				// OTP SMTP
				// ==================================================
				"app.mail.accounts.otp.host=localhost",
				"app.mail.accounts.otp.port=1025",
				"app.mail.accounts.otp.username=test",
				"app.mail.accounts.otp.password=test",
				"app.mail.accounts.otp.from-address=test@example.invalid",
				"app.mail.accounts.otp.from-name=Healthcare MVT Test",
				"app.mail.accounts.otp.smtp-auth=false",
				"app.mail.accounts.otp.starttls=false",
				"app.mail.accounts.otp.connection-timeout-ms=10000",
				"app.mail.accounts.otp.read-timeout-ms=10000",
				"app.mail.accounts.otp.write-timeout-ms=10000",

				// ==================================================
				// NOTIFICATION SMTP
				// ==================================================
				"app.mail.accounts.notification.host=localhost",
				"app.mail.accounts.notification.port=1025",
				"app.mail.accounts.notification.username=test",
				"app.mail.accounts.notification.password=test",
				"app.mail.accounts.notification.from-address=test@example.invalid",
				"app.mail.accounts.notification.from-name=Healthcare MVT Test Notifications",
				"app.mail.accounts.notification.smtp-auth=false",
				"app.mail.accounts.notification.starttls=false",
				"app.mail.accounts.notification.connection-timeout-ms=10000",
				"app.mail.accounts.notification.read-timeout-ms=10000",
				"app.mail.accounts.notification.write-timeout-ms=10000",

				// ==================================================
				// SUPPORT SMTP
				// ==================================================
				"app.mail.accounts.support.host=localhost",
				"app.mail.accounts.support.port=1025",
				"app.mail.accounts.support.username=test",
				"app.mail.accounts.support.password=test",
				"app.mail.accounts.support.from-address=test@example.invalid",
				"app.mail.accounts.support.from-name=Healthcare MVT Test Support",
				"app.mail.accounts.support.smtp-auth=false",
				"app.mail.accounts.support.starttls=false",
				"app.mail.accounts.support.connection-timeout-ms=10000",
				"app.mail.accounts.support.read-timeout-ms=10000",
				"app.mail.accounts.support.write-timeout-ms=10000"
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