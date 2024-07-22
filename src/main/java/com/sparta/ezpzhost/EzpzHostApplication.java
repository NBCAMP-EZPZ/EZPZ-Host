package com.sparta.ezpzhost;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class EzpzHostApplication {

	public static void main(String[] args) {
		SpringApplication.run(EzpzHostApplication.class, args);
	}

}
