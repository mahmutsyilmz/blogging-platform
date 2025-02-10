package com.turkcell.blog.controller;

import com.turkcell.blog.dto.request.LoginDtoRequest;
import com.turkcell.blog.dto.request.PostDtoRequest;
import com.turkcell.blog.dto.request.RegisterDtoRequest;
import com.turkcell.blog.dto.response.ApiResponse;
import com.turkcell.blog.dto.response.AuthenticationDtoResponse;
import com.turkcell.blog.dto.response.PostDtoResponse;
import com.turkcell.blog.dto.response.UserDtoResponse;
import com.turkcell.blog.entity.User;
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
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userServiceImpl;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationDtoResponse> register(
            @Valid @RequestBody RegisterDtoRequest request
    ) {
        AuthenticationDtoResponse response = userServiceImpl.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationDtoResponse> login(
            @Valid @RequestBody LoginDtoRequest request
    ) {
        AuthenticationDtoResponse response = userServiceImpl.authenticate(request);
        return ResponseEntity.ok(response);
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
                .path("/user/deleteUser/" + userId)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserDtoResponse>>> getAllUsers() {
        List<UserDtoResponse> response = userServiceImpl.getAllUsers();

        ApiResponse<List<UserDtoResponse>> apiResponse = ApiResponse.<List<UserDtoResponse>>builder()
                .createdDate(LocalDateTime.now())
                .path("/user/users")
                .data(response)
                .message("Users fetched successfully")
                .build();

        return ResponseEntity.ok(apiResponse);
    }






}