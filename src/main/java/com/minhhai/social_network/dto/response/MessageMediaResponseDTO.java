package com.minhhai.social_network.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class MessageMediaResponseDTO implements Serializable {
    private Long id;
    private String mediaType;
    private String mediaUrl;
}
