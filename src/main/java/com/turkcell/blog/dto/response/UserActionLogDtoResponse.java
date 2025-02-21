package com.turkcell.blog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserActionLogDtoResponse {
    private String username;
    private String action;
    private String timestamp;
}
