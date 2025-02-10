package com.turkcell.blog.config;

import com.turkcell.blog.entity.Role;
import com.turkcell.blog.entity.User;
import com.turkcell.blog.repository.RoleRepository;
import com.turkcell.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

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
    /*
      Şifrelerin encode edilmesinde kullanılacak Bean.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
      AuthenticationManager Bean’i (Spring Security'nin kimlik doğrulama için kullandığı mekanizma).
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }
}