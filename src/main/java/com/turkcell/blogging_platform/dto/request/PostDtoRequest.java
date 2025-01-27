package com.turkcell.blogging_platform.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDtoRequest {

        private String title;
        private String content;
        private UUID userId;
}
