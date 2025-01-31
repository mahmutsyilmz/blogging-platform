package com.turkcell.blog.service;

import com.turkcell.blog.dto.request.LikeDtoRequest;
import com.turkcell.blog.dto.response.LikeDtoResponse;

import java.util.List;
import java.util.UUID;


public interface LikeService {


    LikeDtoResponse likePost(LikeDtoRequest request, UUID userUuid);
    LikeDtoResponse unlikePost(LikeDtoRequest request, UUID userUuid );
    int getLikeCountByPostId(UUID postUuid);
    List<String> getLikedUsernamesByPostId(UUID postUuid);
}
