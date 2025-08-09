package com.minhhai.social_network.dto.request;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class MessageRequestDTO implements Serializable {
    private String content;
}
