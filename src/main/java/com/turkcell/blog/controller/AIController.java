package com.turkcell.blog.controller;

import com.turkcell.blog.dto.response.ApiResponse;
import com.turkcell.blog.service.impl.GoogleGeminiServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AIController {

    private final GoogleGeminiServiceImpl googleGeminiService;

    @PostMapping("/summary")
    public ResponseEntity<ApiResponse<String>> getSummary(@RequestBody Map<String, String> request) {
        // Beklenen anahtar "content" olmalı
        String content = request.get("content");
        String summary = googleGeminiService.summarizeText(content);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .createdDate(LocalDateTime.now())
                .data(summary)
                .message("Summary generated successfully")
                .path("/ai/summary")
                .build();
        return ResponseEntity.ok(response);
    }
}
