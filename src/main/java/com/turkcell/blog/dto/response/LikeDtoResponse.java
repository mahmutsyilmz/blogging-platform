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

    private UUID userUuid;
    private String firstName;
    private String email;

}
