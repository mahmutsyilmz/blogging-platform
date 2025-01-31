package com.turkcell.blog.repository;


import com.turkcell.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;
import java.util.UUID;


public interface UserRepository extends JpaRepository<User, Long> {
    //findByUuid
    Optional<User> findByUuid(UUID uuid);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}

