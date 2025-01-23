package com.turkcell.blogging_platform.repository;

import com.turkcell.blogging_platform.entity.Role;
import com.turkcell.blogging_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByRole(Role role);
}
