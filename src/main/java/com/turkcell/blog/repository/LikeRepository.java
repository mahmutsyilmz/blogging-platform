package com.turkcell.blog.repository;

import com.turkcell.blog.entity.Like;
import com.turkcell.blog.entity.Post;
import com.turkcell.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserAndPost(User user, Post post);
    boolean existsByUserAndPost(User user, Post post);
    int countByPost_Uuid(UUID uuid);
    List<Like> findByPost_Uuid(UUID uuid);
    int countByUser(User user);
}

