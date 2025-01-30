package com.turkcell.blogging_platform.controller;

import com.turkcell.blogging_platform.dto.request.PostDtoRequest;
import com.turkcell.blogging_platform.dto.response.ApiResponse;
import com.turkcell.blogging_platform.dto.response.PostDtoResponse;
import com.turkcell.blogging_platform.service.PostService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user/posts")
@AllArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
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
    @PreAuthorize("isAuthenticated()")
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

    @DeleteMapping("/delete/{postId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PostDtoResponse>> deletePost(
            @PathVariable UUID postId
    ){
        PostDtoResponse postDtoResponse = postService.getPost(postId);

        ApiResponse<PostDtoResponse> apiResponse = ApiResponse.<PostDtoResponse>builder()
                .createdDate(LocalDateTime.now())
                .data(postDtoResponse)
                .path("/api/user/posts/delete" + postId)
                .message("Post başarıyla silindi.")
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
                .message("Post başarıyla getirildi.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<List<PostDtoResponse>>> getAllPosts() {
        // Servisten tüm postları çek
        List<PostDtoResponse> posts = postService.getAllPosts();

        // ApiResponse oluştur
        ApiResponse<List<PostDtoResponse>> apiResponse = ApiResponse.<List<PostDtoResponse>>builder()
                .createdDate(LocalDateTime.now())
                .data(posts)
                .message("Tüm postlar başarıyla getirildi.")
                .path("/api/user/posts")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<PostDtoResponse>>> getAllPostsByUser(
            @PathVariable UUID userId
    ) {
        List<PostDtoResponse> posts = postService.getAllPostsByUser(userId);

        ApiResponse<List<PostDtoResponse>> apiResponse = ApiResponse.<List<PostDtoResponse>>builder()
                .createdDate(LocalDateTime.now())
                .data(posts)
                .message("Kullanıcıya ait postlar başarıyla getirildi.")
                .path("/api/user/posts/" + userId)
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
                .message("Belirtilen başlığa uygun postlar getirildi.")
                .path("/api/user/posts/search?title=" + title)
                .build();

        return ResponseEntity.ok(apiResponse);
    }


}
