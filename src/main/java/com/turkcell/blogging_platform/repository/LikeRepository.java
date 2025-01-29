package com.turkcell.blogging_platform.repository;

import com.turkcell.blogging_platform.entity.Like;
import com.turkcell.blogging_platform.entity.Post;
import com.turkcell.blogging_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, UUID> {

    boolean existsByUserAndPost(User user, Post post);

    Optional<Like> findByUserAndPost(User user, Post post);

    int countByPostId(UUID postId);

    List<Like> findByPostId(UUID postId);

}

