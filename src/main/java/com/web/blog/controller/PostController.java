package com.web.blog.controller;

import com.web.blog.dto.request.PostDtoRequest;
import com.web.blog.dto.response.ApiResponse;
import com.web.blog.dto.response.PostDtoResponse;
import com.web.blog.entity.User;
import com.web.blog.service.PostService;
import com.web.blog.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/post")
@AllArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserServiceImpl userServiceImpl;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<PostDtoResponse>> createPost(
            @Valid @RequestBody PostDtoRequest request,
            Authentication authentication) {

        User user = userServiceImpl.getCurrentUser(authentication);

        PostDtoResponse response = postService.createPost(request, user);

        ApiResponse<PostDtoResponse> apiResponse = ApiResponse.<PostDtoResponse>builder()
                .createdDate(LocalDateTime.now())
                .data(response)
                .message("Post created successfully")
                .path("/api/user/posts/create")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/update/{postId}")
    public ResponseEntity<ApiResponse<PostDtoResponse>> updatePost(
            @Valid @RequestBody PostDtoRequest request,
            @PathVariable UUID postId
    ) {
        PostDtoResponse postDtoResponse = postService.updatePost(request, postId);

        ApiResponse<PostDtoResponse> apiResponse = ApiResponse.<PostDtoResponse>builder()
                .createdDate(LocalDateTime.now())
                .data(postDtoResponse)
                .message("Post successfully updated")
                .path("/api/user/posts/" + postId)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<ApiResponse<PostDtoResponse>> deletePost(
            @PathVariable UUID postId
    ){
        PostDtoResponse postDtoResponse = postService.getPost(postId);

        ApiResponse<PostDtoResponse> apiResponse = ApiResponse.<PostDtoResponse>builder()
                .createdDate(LocalDateTime.now())
                .data(postDtoResponse)
                .path("/api/user/posts/delete" + postId)
                .message("Post successfully deleted")
                .build();

        postService.deletePost(postId);

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/get/{postId}")
    public ResponseEntity<ApiResponse<PostDtoResponse>> getPost(
            @PathVariable UUID postId
    ){
        PostDtoResponse postDtoResponse = postService.getPost(postId);

        ApiResponse<PostDtoResponse> apiResponse = ApiResponse.<PostDtoResponse>builder()
                .createdDate(LocalDateTime.now())
                .data(postDtoResponse)
                .path("/api/user/posts/get" + postId)
                .message("Post successfully get")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<List<PostDtoResponse>>> getAllPosts() {

        List<PostDtoResponse> posts = postService.getAllPosts();

        ApiResponse<List<PostDtoResponse>> apiResponse = ApiResponse.<List<PostDtoResponse>>builder()
                .createdDate(LocalDateTime.now())
                .data(posts)
                .message("All posts successfully get")
                .path("/api/user/posts")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/getAllPostsByUserId/{userId}")
    public ResponseEntity<ApiResponse<List<PostDtoResponse>>> getAllPostsByUser(
            @PathVariable UUID userId
    ) {
        List<PostDtoResponse> posts = postService.getAllPostsByUser(userId);

        ApiResponse<List<PostDtoResponse>> apiResponse = ApiResponse.<List<PostDtoResponse>>builder()
                .createdDate(LocalDateTime.now())
                .data(posts)
                .message("Successfully get all posts by user")
                .path("/posts/getAllPostsByUserId/" + userId)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PostDtoResponse>>> getAllPostsByTitle(
            @RequestParam String title
    ) {
        List<PostDtoResponse> posts = postService.getAllPostsByTitle(title);

        ApiResponse<List<PostDtoResponse>> apiResponse = ApiResponse.<List<PostDtoResponse>>builder()
                .createdDate(LocalDateTime.now())
                .data(posts)
                .message("Successfully get all posts by title")
                .path("/api/user/posts/search?title=" + title)
                .build();

        return ResponseEntity.ok(apiResponse);
    }



}
