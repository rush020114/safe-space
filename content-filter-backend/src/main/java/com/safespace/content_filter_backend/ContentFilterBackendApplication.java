package com.safespace.content_filter_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ContentFilterBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContentFilterBackendApplication.class, args);
	}

}
