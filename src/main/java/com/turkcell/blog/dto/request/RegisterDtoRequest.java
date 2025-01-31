package com.turkcell.blog.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
public class RegisterDtoRequest {

    @Schema(description = "Username", example = "username")
    @NotBlank(message = "Username cannot be empty.")
    @Size(min = 3, max = 20, message = "Username must be between 3-20 characters.")
    private String username;

    @Schema(description = "Password", example = "Password.")
    @NotBlank(message = "Password cannot be empty.")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[!@#$%^&*()_+{}\\[\\]:;'\",.<>?/~`-]).{8,}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one special character and be at least 8 characters long."
    )
    @Size(min = 6, max = 20, message = "Password must be between 6-20 characters.")
    private String password;



    @Schema(description = "First Name", example = "first name")
    @NotBlank(message = "First Name cannot be empty.")
    private String firstName;

    @Schema(description = "Email", example = "email@gmail.com")
    @NotBlank(message = "Email cannot be empty.")
    @Email(message = "Email is not valid.")
    private String email;

    @Schema(description = "Last Name", example = "last name")
    @NotBlank(message = "Last Name cannot be empty.")
    private String lastName;


}
