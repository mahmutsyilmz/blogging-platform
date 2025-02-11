package com.turkcell.blog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDtoResponse {

    private UUID uuid;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String role;
    private String createdDate;
    private String bio;
    private boolean emailVerified;
    private Integer postCount;
    private Integer likeCount;

}
