package com.minhhai.social_network.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class CommentResponseDTO implements Serializable {
    private Long id;

    private String content;

    private UserResponseDTO userCreated;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long countReactions;
}
