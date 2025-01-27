package com.turkcell.blogging_platform.controller;

import com.turkcell.blogging_platform.dto.request.PostDtoRequest;
import com.turkcell.blogging_platform.dto.response.ApiResponse;
import com.turkcell.blogging_platform.dto.response.PostDtoResponse;
import com.turkcell.blogging_platform.service.PostService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

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
}
