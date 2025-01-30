package com.turkcell.blogging_platform.controller;

import com.turkcell.blogging_platform.dto.request.LikeDtoRequest;
import com.turkcell.blogging_platform.dto.response.ApiResponse;
import com.turkcell.blogging_platform.dto.response.LikeDtoResponse;
import com.turkcell.blogging_platform.service.LikeService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user/like")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<LikeDtoResponse>> likePost(
            @RequestBody LikeDtoRequest request,
            Authentication authentication) {

        String username = authentication.getName();

        LikeDtoResponse response = likeService.likePost(request, username);

        ApiResponse<LikeDtoResponse> apiResponse = ApiResponse.<LikeDtoResponse>builder()
                .createdDate(LocalDateTime.now())
                .data(response)
                .message("Post başarıyla beğenildi")
                .path("/api/user/like/create")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/delete")
    public ResponseEntity<ApiResponse<LikeDtoResponse>> unlikePost(
            @RequestBody LikeDtoRequest request,
            Authentication authentication) {

        String username = authentication.getName();

        LikeDtoResponse response = likeService.unlikePost(request, username);

        ApiResponse<LikeDtoResponse> apiResponse = ApiResponse.<LikeDtoResponse>builder()
                .createdDate(LocalDateTime.now())
                .data(response)
                .message("Post beğenisi kaldırıldı")
                .path("/api/user/like/delete")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/count/{postId}")
    public ResponseEntity<ApiResponse<Integer>> getLikeCountByPostId(
            @PathVariable UUID postId
    ) {
        int response = likeService.getLikeCountByPostId(postId);

        ApiResponse<Integer> apiResponse = ApiResponse.<Integer>builder()
                .createdDate(LocalDateTime.now())
                .data(response)
                .message("Post beğeni sayısı getirildi")
                .path("/api/user/like/count/" + postId)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/likedUsers/{postId}")
    public ResponseEntity<ApiResponse<List<String>>> getLikedUsernamesByPostId(
            @PathVariable UUID postId
    ) {
        List<String> response = likeService.getLikedUsernamesByPostId(postId);

        ApiResponse<List<String>> apiResponse = ApiResponse.<List<String>>builder()
                .createdDate(LocalDateTime.now())
                .data(response)
                .message("Post beğenen kullanıcılar getirildi")
                .path("/api/user/like/likedUsers/" + postId)
                .build();
        return ResponseEntity.ok(apiResponse);
    }



}
