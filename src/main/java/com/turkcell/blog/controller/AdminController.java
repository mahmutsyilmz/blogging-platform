package com.turkcell.blog.controller;

import com.turkcell.blog.dto.response.*;
import com.turkcell.blog.service.PostRequestService;
import com.turkcell.blog.service.impl.PostServiceImpl;
import com.turkcell.blog.service.impl.UserServiceImpl;
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
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserServiceImpl userServiceImpl;
    private final PostServiceImpl postServiceImpl;
    private final PostRequestService postRequestService;



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

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PostRequestDtoResponse>>> getPendingRequests() {
        List<PostRequestDtoResponse> pendingRequests = postRequestService.getAllPendingRequests();

        ApiResponse<List<PostRequestDtoResponse>> apiResponse = ApiResponse.<List<PostRequestDtoResponse>>builder()
                .data(pendingRequests)
                .createdDate(LocalDateTime.now())
                .message("Pending requests are fetched.")
                .path("/admin/pending")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/approve/{requestUuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PostRequestDtoResponse>> approveRequest(@PathVariable UUID requestUuid) {
        PostRequestDtoResponse responseDto = postRequestService.approveRequest(requestUuid);

        ApiResponse<PostRequestDtoResponse> apiResponse = ApiResponse.<PostRequestDtoResponse>builder()
                .data(responseDto)
                .path("/admin/approve/" + requestUuid)
                .createdDate(LocalDateTime.now())
                .message(responseDto.getRequestType() + " request approved.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/reject/{requestUuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PostRequestDtoResponse>> rejectRequest(@PathVariable UUID requestUuid) {
        PostRequestDtoResponse responseDto = postRequestService.rejectRequest(requestUuid);

        ApiResponse<PostRequestDtoResponse> apiResponse = ApiResponse.<PostRequestDtoResponse>builder()
                .data(responseDto)
                .path("/admin/approve/" + requestUuid)
                .createdDate(LocalDateTime.now())
                .message(responseDto.getRequestType() + " request rejected.")
                .build();


        return ResponseEntity.ok(apiResponse);
    }

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

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardDto>> getDashboard() {
        DashboardDto dashboard = userServiceImpl.getDashboard();

        ApiResponse<DashboardDto> apiResponse = ApiResponse.<DashboardDto>builder()
                .createdDate(LocalDateTime.now())
                .data(dashboard)
                .message("Dashboard fetched successfully")
                .path("/admin/dashboard")
                .build();

        return ResponseEntity.ok(apiResponse);
    }







}
