package com.minhhai.social_network.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class ReactionResponseDTO implements Serializable {
    private Long id;

    private UserResponseDTO userReaction;
}
