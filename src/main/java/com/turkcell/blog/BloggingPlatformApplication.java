package com.turkcell.blog;

import com.turkcell.blog.entity.Role;
import com.turkcell.blog.entity.User;
import com.turkcell.blog.repository.RoleRepository;
import com.turkcell.blog.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableJpaAuditing
public class BloggingPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(BloggingPlatformApplication.class, args);
	}

	@Bean
	CommandLineRunner init(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {



			if (!roleRepository.findByName("ADMIN").isPresent()) {
				Role adminRole = new Role();
				adminRole.setName("ADMIN");
				roleRepository.save(adminRole);
			}

			if (!roleRepository.findByName("USER").isPresent()) {
				Role userRole = new Role();
				userRole.setName("USER");
				roleRepository.save(userRole);
			}

			if (!userRepository.findByUsername("admin").isPresent()) {
				Role adminRole = roleRepository.findByName("ADMIN")
						.orElseThrow(() -> new RuntimeException("Error: Role is not found."));

				User adminUser = User.builder()
						.username("admin")
						.email("admin@example.com")
						.firstName("Admin")
						.lastName("User")
						.password(passwordEncoder.encode("Admin123."))
						.role(adminRole)
						.build();

				userRepository.save(adminUser);

			}
		};

	}
}
