package com.turkcell.blogging_platform.dto.request;

import com.turkcell.blogging_platform.entity.Post;
import com.turkcell.blogging_platform.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LikeDtoRequest {

    private UUID postId;
}
