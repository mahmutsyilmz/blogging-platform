package com.web.blog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostRequestDtoResponse {

    private Long id;
    private UUID uuid;
    private String title;
    private String content;
    private String username;
    private String email;
    private Long targetPostId;
    private String createdDate;
    private String requestStatus;
    private String requestType;

}
