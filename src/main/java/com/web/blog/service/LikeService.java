package com.web.blog.service;

import com.web.blog.dto.request.LikeDtoRequest;
import com.web.blog.dto.response.LikeDtoResponse;

import java.util.List;
import java.util.UUID;


public interface LikeService {


    LikeDtoResponse likePost(LikeDtoRequest request, UUID userUuid);
    LikeDtoResponse unlikePost(LikeDtoRequest request, UUID userUuid );
    int getLikeCountByPostId(UUID postUuid);
    List<String> getLikedUsernamesByPostId(UUID postUuid);
}
