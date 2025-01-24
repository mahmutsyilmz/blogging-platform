package com.turkcell.blogging_platform.dto.request;

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
public class LoginRequest {
        @Schema(description = "Username", example = "username")
        @NotBlank(message = "Kullanıcı adı boş olamaz.")
        private String username;
        @NotBlank(message = "Şifre boş olamaz.")
        @Schema(description = "Password", example = "password")
        private String password;
}
