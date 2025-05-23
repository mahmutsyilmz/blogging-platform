package com.web.blog.repository;

import com.web.blog.entity.Post;
import com.web.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, Long> {


    List<Post> findByUser(User user);
    Optional<Post> findByUuid(UUID uuid);

    Optional<Post> findById(Long id);
    List<Post> findByTitleContainingIgnoreCase(String title);

    Integer countByUser(User user);
}
