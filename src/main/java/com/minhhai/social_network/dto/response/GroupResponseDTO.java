package com.minhhai.social_network.dto.response;

import com.minhhai.social_network.util.enums.Privacy;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class GroupResponseDTO implements Serializable {
    private Long id;
    private String name;
    private String description;
    private Privacy privacy;
    private Long userIdCreated;
}
