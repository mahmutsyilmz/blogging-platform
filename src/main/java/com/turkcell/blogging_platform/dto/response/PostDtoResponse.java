package com.turkcell.blogging_platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDtoResponse {
    private UUID id;
    private String title;
    private String content;
    private String username;
    private String createdDate;
    private String updatedDate;
    private int likeCount;

}
