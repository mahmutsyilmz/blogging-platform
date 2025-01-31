package com.turkcell.blog.controller;

import com.turkcell.blog.dto.request.LoginDtoRequest;
import com.turkcell.blog.dto.request.RegisterDtoRequest;
import com.turkcell.blog.dto.response.ApiResponse;
import com.turkcell.blog.dto.response.AuthenticationDtoResponse;
import com.turkcell.blog.dto.response.PostDtoResponse;
import com.turkcell.blog.dto.response.UserDtoResponse;
import com.turkcell.blog.service.impl.PostServiceImpl;
import com.turkcell.blog.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserServiceImpl userServiceImpl;
    private final PostServiceImpl postServiceImpl;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationDtoResponse> login(
            @Valid @RequestBody LoginDtoRequest request
    ) {
        AuthenticationDtoResponse response = userServiceImpl.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserDtoResponse>>> getAllUsers() {
        List<UserDtoResponse> response = userServiceImpl.getAllUsers();

        ApiResponse<List<UserDtoResponse>> apiResponse = ApiResponse.<List<UserDtoResponse>>builder()
                .createdDate(LocalDateTime.now())
                .path("/admin/users")
                .data(response)
                .message("Users fetched successfully")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createUser")
    public ResponseEntity<ApiResponse<UserDtoResponse>> createUser(
            @Valid @RequestBody RegisterDtoRequest request
    ) {
        UserDtoResponse response = userServiceImpl.createUser(request);
        ApiResponse<UserDtoResponse> apiResponse = ApiResponse.<UserDtoResponse>builder()
                .createdDate(LocalDateTime.now())
                .data(response)
                .message("User created successfully")
                .path("/admin/createUser")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<List<PostDtoResponse>>> getAllPosts() {
        List<PostDtoResponse> response = postServiceImpl.getAllPosts();

        ApiResponse<List<PostDtoResponse>> apiResponse = ApiResponse.<List<PostDtoResponse>>builder()
                .createdDate(LocalDateTime.now())
                .path("/admin/posts")
                .data(response)
                .message("Posts fetched successfully")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<ApiResponse<UserDtoResponse>> deleteUser(
            @PathVariable UUID userId
            ) {
        UserDtoResponse response = userServiceImpl.deleteUser(userId);

        ApiResponse<UserDtoResponse> apiResponse = ApiResponse.<UserDtoResponse>builder()
                .createdDate(LocalDateTime.now())
                .data(response)
                .message("User deleted successfully")
                .path("/admin/deleteUser/" + userId)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deletePost/{postId}")
    public ResponseEntity<ApiResponse<PostDtoResponse>> deletePost(
            @PathVariable UUID postId
    ) {
        PostDtoResponse response = postServiceImpl.getPost(postId);

        ApiResponse<PostDtoResponse> apiResponse = ApiResponse.<PostDtoResponse>builder()
                .createdDate(LocalDateTime.now())
                .data(response)
                .message("Post deleted successfully")
                .path("/admin/deletePost/" + postId)
                .build();

        postServiceImpl.deletePost(postId);

        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getPost/{postId}")
    public ResponseEntity<ApiResponse<PostDtoResponse>> getPost(
            @PathVariable UUID postId
    ) {
        PostDtoResponse response = postServiceImpl.getPost(postId);

        ApiResponse<PostDtoResponse> apiResponse = ApiResponse.<PostDtoResponse>builder()
                .createdDate(LocalDateTime.now())
                .data(response)
                .message("Post fetched successfully")
                .path("/admin/getPost/" + postId)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getPostsByUser/{userId}")
    public ResponseEntity<ApiResponse<List<PostDtoResponse>>> getAllPostsByUser(
            @PathVariable UUID userId
    ) {
        List<PostDtoResponse> response = postServiceImpl.getAllPostsByUser(userId);

        ApiResponse<List<PostDtoResponse>> apiResponse = ApiResponse.<List<PostDtoResponse>>builder()
                .createdDate(LocalDateTime.now())
                .data(response)
                .message("Posts fetched successfully")
                .path("/admin/getPostsByUser/" + userId)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

}
