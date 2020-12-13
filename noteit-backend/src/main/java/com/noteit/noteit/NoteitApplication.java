package com.noteit.noteit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.validation.annotation.Validated;

@SpringBootApplication
@Validated
public class NoteitApplication {
	public static void main(String[] args) {
		SpringApplication.run(NoteitApplication.class, args);
	}
}
