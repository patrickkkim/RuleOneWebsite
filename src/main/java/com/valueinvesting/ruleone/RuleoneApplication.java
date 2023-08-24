package com.valueinvesting.ruleone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.validation.annotation.Validated;

@SpringBootApplication
@Validated
public class RuleoneApplication {

	public static void main(String[] args) {
		SpringApplication.run(RuleoneApplication.class, args);
	}

}
