package com.minhhai.social_network.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
public class CreatePostRequestDTO implements Serializable {
    private String content;
    private Set<MediaRequestDTO> postMedia;
}
