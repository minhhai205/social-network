package com.minhhai.social_network.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class MediaRequestDTO implements Serializable {
    private String name;
    private String type;
    private String base64Data;
}
