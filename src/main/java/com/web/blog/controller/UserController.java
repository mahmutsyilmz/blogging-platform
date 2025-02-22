package com.web.blog.controller;

import com.web.blog.dto.request.LoginDtoRequest;
import com.web.blog.dto.request.RegisterDtoRequest;
import com.web.blog.dto.request.UpdateUserDtoRequest;
import com.web.blog.dto.response.ApiResponse;
import com.web.blog.dto.response.AuthenticationDtoResponse;
import com.web.blog.dto.response.UserDtoResponse;
import com.web.blog.entity.User;
import com.web.blog.service.EmailService;
import com.web.blog.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userServiceImpl;
    private final EmailService emailService;

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

    @GetMapping("/myProfile")
    public ResponseEntity<ApiResponse<UserDtoResponse>> getMyProfile(
            Authentication authentication
    ) {

        User user = userServiceImpl.getCurrentUser(authentication);

        UserDtoResponse response = userServiceImpl.getUserProfile(user.getUuid());

        ApiResponse<UserDtoResponse> apiResponse = ApiResponse.<UserDtoResponse>builder()
                .createdDate(LocalDateTime.now())
                .path("/user/myProfile")
                .data(response)
                .message("Profile fetched successfully")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/profile/{userUuid}")
    public ResponseEntity<ApiResponse<UserDtoResponse>> getProfile(
            @PathVariable UUID userUuid
    ) {
        UserDtoResponse response = userServiceImpl.getUserProfile(userUuid);

        ApiResponse<UserDtoResponse> apiResponse = ApiResponse.<UserDtoResponse>builder()
                .createdDate(LocalDateTime.now())
                .path("/user/profile")
                .data(response)
                .message("Profile fetched successfully")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/update")
    public ResponseEntity<ApiResponse<UserDtoResponse>> updateUser(
            @Valid @RequestBody UpdateUserDtoRequest request,
            Authentication authentication
    ) {
        User user = userServiceImpl.getCurrentUser(authentication);
        UserDtoResponse response = userServiceImpl.updateUser(user.getUuid(), request);

        ApiResponse<UserDtoResponse> apiResponse = ApiResponse.<UserDtoResponse>builder()
                .createdDate(LocalDateTime.now())
                .path("/user/update/" + user.getUuid())
                .data(response)
                .message("User updated successfully")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/sendVerification")
    public ResponseEntity<ApiResponse<String>> sendVerificationEmail(Authentication authentication) {
        User user = userServiceImpl.getCurrentUser(authentication);


        // Rastgele 6 haneli bir doğrulama kodu oluşturuyoruz
        String verificationCode = String.format("%06d", new Random().nextInt(999999));
        userServiceImpl.saveVerificationCode(user.getUuid(), verificationCode);

        // E-posta gönderme
        emailService.sendVerificationEmail(user.getEmail(), verificationCode);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .createdDate(LocalDateTime.now())
                .data("Verification code sent")
                .message("Verification email sent successfully")
                .path("/user/sendVerification")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/verifyEmail")
    public ResponseEntity<ApiResponse<UserDtoResponse>> verifyEmail(@RequestParam String code, Authentication authentication) {
        User user = userServiceImpl.getCurrentUser(authentication);

        boolean verified = userServiceImpl.verifyUserEmail(user.getUuid(), code);
        if (!verified) {
            return ResponseEntity.badRequest().body(ApiResponse.<UserDtoResponse>builder()
                    .createdDate(LocalDateTime.now())
                    .data(null)
                    .message("Invalid verification code")
                    .path("/user/verifyEmail")
                    .build());
        }
        UserDtoResponse response = userServiceImpl.getUserProfile(user.getUuid());
        ApiResponse<UserDtoResponse> apiResponse = ApiResponse.<UserDtoResponse>builder()
                .createdDate(LocalDateTime.now())
                .data(response)
                .message("Email verified successfully")
                .path("/user/verifyEmail")
                .build();
        return ResponseEntity.ok(apiResponse);
    }







}