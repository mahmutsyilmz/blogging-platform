package com.turkcell.blogging_platform.repository;

import com.turkcell.blogging_platform.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {


}
