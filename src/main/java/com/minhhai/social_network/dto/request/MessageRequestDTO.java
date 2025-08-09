package com.minhhai.social_network.dto.request;

import lombok.Getter;

import java.io.Serializable;
import java.util.Set;

@Getter
public class MessageRequestDTO implements Serializable {
    private String content;
    private Set<MessageMediaRequestDTO> messageMedia;
}
