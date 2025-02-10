package com.turkcell.blog.controller;

import com.turkcell.blog.dto.request.LikeDtoRequest;
import com.turkcell.blog.dto.response.ApiResponse;
import com.turkcell.blog.dto.response.LikeDtoResponse;
import com.turkcell.blog.entity.User;
import com.turkcell.blog.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<LikeDtoResponse>> likePost(
            @RequestBody LikeDtoRequest request,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        LikeDtoResponse response = likeService.likePost(request, user.getUuid());

        ApiResponse<LikeDtoResponse> apiResponse = ApiResponse.<LikeDtoResponse>builder()
                .createdDate(LocalDateTime.now())
                .data(response)
                .message("Post successfully liked")
                .path("like/create")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<LikeDtoResponse>> unlikePost(
            @RequestBody LikeDtoRequest request,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        LikeDtoResponse response = likeService.unlikePost(request, user.getUuid());

        ApiResponse<LikeDtoResponse> apiResponse = ApiResponse.<LikeDtoResponse>builder()
                .createdDate(LocalDateTime.now())
                .data(response)
                .message("Post successfully unliked")
                .path("like/delete")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/count/{postUuid}")
    public ResponseEntity<ApiResponse<Integer>> getLikeCountByPostId(
            @PathVariable UUID postUuid
    ) {
        int response = likeService.getLikeCountByPostId(postUuid);

        ApiResponse<Integer> apiResponse = ApiResponse.<Integer>builder()
                .createdDate(LocalDateTime.now())
                .data(response)
                .message("Like count is fetched.")
                .path("like/count/" + postUuid)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/likedUsers/{postUuid}")
    public ResponseEntity<ApiResponse<List<String>>> getLikedUsernamesByPostId(
            @PathVariable UUID postUuid
    ) {
        List<String> response = likeService.getLikedUsernamesByPostId(postUuid);

        ApiResponse<List<String>> apiResponse = ApiResponse.<List<String>>builder()
                .createdDate(LocalDateTime.now())
                .data(response)
                .message("Users who liked the post are fetched.")
                .path("like/likedUsers/" + postUuid)
                .build();
        return ResponseEntity.ok(apiResponse);
    }



}
