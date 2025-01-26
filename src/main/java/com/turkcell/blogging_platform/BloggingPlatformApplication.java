package com.turkcell.blogging_platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BloggingPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(BloggingPlatformApplication.class, args);
	}

}
