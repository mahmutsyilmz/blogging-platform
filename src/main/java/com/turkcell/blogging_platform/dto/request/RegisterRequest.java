package com.turkcell.blogging_platform.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Register Request")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @Schema(description = "Username", example = "username")
    @NotBlank(message = "Kullanıcı adı boş olamaz.")
    @Size(min = 3, max = 20, message = "Kullanıcı adı 3-20 karakter arasında olmalıdır.")
    private String username;
    @Schema(description = "Password", example = "password")
    @NotBlank(message = "Şifre boş olamaz.")
    @Size(min = 6, max = 20, message = "Şifre 6-20 karakter arasında olmalıdır.")
    private String password;
    @Schema(description = "First Name", example = "first name")
    @NotBlank(message = "Ad boş olamaz.")
    private String firstName;
    @Schema(description = "Last Name", example = "last name")
    @NotBlank(message = "Soyad boş olamaz.")
    private String lastName;

}
