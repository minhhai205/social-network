package com.minhhai.social_network.dto.request;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class MessageMediaRequestDTO implements Serializable {
    private String name;
    private String type;
    private String base64Data;
}
