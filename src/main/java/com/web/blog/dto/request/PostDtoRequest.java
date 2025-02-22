package com.web.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDtoRequest {

        @NotBlank(message = "Title can not be empty")
        @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
        private String title;
        @NotBlank(message = "Content can not be empty")
        @Size(min = 10, message = "Content must be at least 10 characters")
        private String content;
}
