package com.turkcell.blog.controller;

import com.turkcell.blog.dto.request.LoginDtoRequest;
import com.turkcell.blog.dto.request.PostDtoRequest;
import com.turkcell.blog.dto.request.RegisterDtoRequest;
import com.turkcell.blog.dto.response.ApiResponse;
import com.turkcell.blog.dto.response.AuthenticationDtoResponse;
import com.turkcell.blog.dto.response.PostDtoResponse;
import com.turkcell.blog.dto.response.UserDtoResponse;
import com.turkcell.blog.entity.PostRequest;
import com.turkcell.blog.service.PostRequestService;
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
    public ResponseEntity<ApiResponse<List<PostRequest>>> getPendingRequests() {
        List<PostRequest> pendingRequests = postRequestService.getAllPendingRequests();

        ApiResponse<List<PostRequest>> apiResponse = ApiResponse.<List<PostRequest>>builder()
                .data(pendingRequests)
                .createdDate(LocalDateTime.now())
                .message("Pending requests are fetched.")
                .path("/admin/pending")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/approve/{requestUuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PostRequest>> approveRequest(@PathVariable UUID requestUuid) {
        PostRequest request = postRequestService.approveRequest(requestUuid);

        ApiResponse<PostRequest> apiResponse = ApiResponse.<PostRequest>builder()
                .data(request)
                .path("/admin/approve/" + requestUuid)
                .createdDate(LocalDateTime.now())
                .message(request.getRequestType() + " request approved.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/reject/{requestUuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PostRequest>> rejectRequest(@PathVariable UUID requestUuid) {
        PostRequest request = postRequestService.rejectRequest(requestUuid);

        ApiResponse<PostRequest> apiResponse = ApiResponse.<PostRequest>builder()
                .data(request)
                .path("/admin/approve/" + requestUuid)
                .createdDate(LocalDateTime.now())
                .message(request.getRequestType() + " request rejected.")
                .build();


        return ResponseEntity.ok(apiResponse);
    }







}
