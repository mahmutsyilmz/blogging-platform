package com.turkcell.blogging_platform.service;

import com.turkcell.blogging_platform.dto.request.LikeDtoRequest;
import com.turkcell.blogging_platform.dto.response.LikeDtoResponse;

import java.util.List;
import java.util.UUID;


public interface LikeService {


    LikeDtoResponse likePost(LikeDtoRequest request, String username );
    LikeDtoResponse unlikePost(LikeDtoRequest request, String username );
    int getLikeCountByPostId(UUID postId);
    List<String> getLikedUsernamesByPostId(UUID postId);
}
