package com.turkcell.blogging_platform.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDtoRequest {

        @NotBlank(message = "Başlık boş olamaz")
        @Size(min = 1, max = 255, message = "Başlık 1-255 karakter arasında olmalıdır.")
        private String title;
        @NotBlank(message = "İçerik boş olamaz")
        @Min(value = 10, message = "İçerik 10 karakterden uzun olmalıdır.")
        private String content;
}
