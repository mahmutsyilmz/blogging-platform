package com.turkcell.blogging_platform.controller;

import com.turkcell.blogging_platform.dto.request.PostDtoRequest;
import com.turkcell.blogging_platform.dto.response.ApiResponse;
import com.turkcell.blogging_platform.dto.response.PostDtoResponse;
import com.turkcell.blogging_platform.service.PostService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/user/posts")
@AllArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<PostDtoResponse>> createPost(
            @Valid @RequestBody PostDtoRequest request,
            Authentication authentication) {

        //authentication.getName() -> Spring Security context'ten kullanıcı adını alır
        String username = authentication.getName();

        PostDtoResponse response = postService.createPost(request, username);

        //ApiReponse nesnesini oluşturuyoruz
        ApiResponse<PostDtoResponse> apiResponse = ApiResponse.<PostDtoResponse>builder()
                .createdDate(LocalDateTime.now())
                .data(response)
                .message("Post başarıyla oluşturuldu")
                .path("/api/user/posts/create")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/update/{postId}")
    public ResponseEntity<ApiResponse<PostDtoResponse>> updatePost(
            @Valid @RequestBody PostDtoRequest request,
            @PathVariable UUID postId
    ) {
        // Servis çağrısı -> orada @PreAuthorize devreye girecek
        PostDtoResponse postDtoResponse = postService.updatePost(request, postId);

        ApiResponse<PostDtoResponse> apiResponse = ApiResponse.<PostDtoResponse>builder()
                .createdDate(LocalDateTime.now())
                .data(postDtoResponse)
                .message("Post başarıyla güncellendi")
                .path("/api/user/posts/" + postId)
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
