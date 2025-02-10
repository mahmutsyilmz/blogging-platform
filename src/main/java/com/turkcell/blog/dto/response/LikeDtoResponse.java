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
public class LikeDtoResponse {

    private UUID uuid;
    private UUID userUuid;
    private String username;
    private String email;
    private String createdDate;
    private UUID postUuid;






}
