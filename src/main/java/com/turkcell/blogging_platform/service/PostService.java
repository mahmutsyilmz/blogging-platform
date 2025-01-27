package com.turkcell.blogging_platform.service;

import com.turkcell.blogging_platform.dto.request.PostDtoRequest;
import com.turkcell.blogging_platform.dto.response.PostDtoResponse;

import java.util.List;
import java.util.UUID;

public interface PostService {


    PostDtoResponse createPost(PostDtoRequest request,String username);

    PostDtoResponse updatePost(PostDtoRequest request, UUID postId);

    void deletePost(UUID postId);

    PostDtoResponse getPost(UUID postId);

    List<PostDtoResponse> getAllPosts();

    List<PostDtoResponse> getAllPostsByUser(UUID userId);

    List<PostDtoResponse> getAllPostsByTitle(String title);

}
