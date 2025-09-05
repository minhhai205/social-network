package com.minhhai.social_network.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CommentRequestDTO implements Serializable {
    @NotBlank(message = "Content must be not null")
    private String content;
}
