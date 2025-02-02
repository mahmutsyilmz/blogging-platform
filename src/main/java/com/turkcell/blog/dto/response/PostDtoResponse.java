package com.turkcell.blog.dto.response;

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

    private String title;
    private String content;
    private String username;
    private String createdDate;
    private String updatedDate;
    private Integer likeCount;

}
