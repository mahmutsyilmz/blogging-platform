package com.turkcell.blogging_platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDtoResponse {
    private String title;
    private String content;
    private String username;
    private String createdDate;
    private String updatedDate;
}
