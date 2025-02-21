package com.turkcell.blog.controller;

import com.turkcell.blog.dto.response.ApiResponse;
import com.turkcell.blog.dto.response.UserActionLogDtoResponse;
import com.turkcell.blog.entity.UserActionLog;
import com.turkcell.blog.service.UserActionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class LogController {

    private final UserActionLogService userActionLogService;

    @GetMapping("/actionLogs")
    public ResponseEntity<ApiResponse<List<UserActionLogDtoResponse>>> getActionLogs() {
        List<UserActionLog> logs = userActionLogService.getAllLogs();
        List<UserActionLogDtoResponse> dtoList = logs.stream()
                .map(log -> new UserActionLogDtoResponse(
                        log.getUsername(),
                        log.getAction(),
                        log.getTimestamp().toString()))
                .collect(Collectors.toList());

        ApiResponse<List<UserActionLogDtoResponse>> response = ApiResponse.<List<UserActionLogDtoResponse>>builder()
                .createdDate(LocalDateTime.now())
                .data(dtoList)
                .message("Action logs fetched successfully")
                .path("/admin/actionLogs")
                .build();
        return ResponseEntity.ok(response);
    }
}
