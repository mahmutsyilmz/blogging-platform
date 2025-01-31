package com.turkcell.blog.service;

import com.turkcell.blog.dto.request.PostDtoRequest;
import com.turkcell.blog.dto.response.PostDtoResponse;
import com.turkcell.blog.entity.User;

import java.util.List;
import java.util.UUID;

public interface PostService {


    PostDtoResponse createPost(PostDtoRequest request, User user);

    PostDtoResponse updatePost(PostDtoRequest request, UUID postId);

    void deletePost(UUID postId);

    PostDtoResponse getPost(UUID postId);

    List<PostDtoResponse> getAllPosts();

    List<PostDtoResponse> getAllPostsByUser(UUID userId);

    List<PostDtoResponse> getAllPostsByTitle(String title);

}
