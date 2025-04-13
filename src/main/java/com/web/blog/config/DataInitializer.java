package com.web.blog.config;

import com.web.blog.entity.Role;
import com.web.blog.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        if (roleRepository.findByName("USER").isEmpty()) {
            Role userRole = Role.builder().name("USER").build();
            roleRepository.save(userRole);
        }

        if (roleRepository.findByName("ADMIN").isEmpty()) {
            Role adminRole = Role.builder().name("ADMIN").build();
            roleRepository.save(adminRole);
        }
    }
}
