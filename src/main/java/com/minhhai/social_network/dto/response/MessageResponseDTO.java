package com.minhhai.social_network.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@Builder
public class MessageResponseDTO implements Serializable {
    private String content;
    private String userIdSent;
    private Set<MessageMediaResponseDTO> messageMedia;
}
