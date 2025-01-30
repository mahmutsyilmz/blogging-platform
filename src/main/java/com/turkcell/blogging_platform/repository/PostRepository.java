package com.turkcell.blogging_platform.repository;

import com.turkcell.blogging_platform.entity.Post;
import com.turkcell.blogging_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {


    List<Post> findByUser(User user);

    List<Post> findByTitleContainingIgnoreCase(String title);
}
