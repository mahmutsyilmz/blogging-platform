package com.web.blog.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Login Request")
public class LoginDtoRequest {
        @Schema(description = "Username", example = "username")
        @NotBlank(message = "Username cannot be empty.")
        private String username;
        @NotBlank(message = "Password cannot be empty.")
        @Schema(description = "Password", example = "Password.")
        private String password;
}
