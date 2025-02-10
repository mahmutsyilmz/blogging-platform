package com.turkcell.blog.repository;

import com.turkcell.blog.entity.PostRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PostRequestRepository extends JpaRepository<PostRequest, Long> {

    PostRequest findByUuid(UUID uuid);

    boolean existsByTargetPostIdAndStatus(Long targetPostId, PostRequest.RequestStatus status);
}
