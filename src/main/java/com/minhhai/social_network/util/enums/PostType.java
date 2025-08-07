package com.minhhai.social_network.util.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum PostType {
    PERSONAL,
    GROUP,
    PAGE,
    UNKNOWN;

    @JsonCreator
    public static PostType fromString(String value) {
        return EnumUtils.fromString(PostType.class, value, UNKNOWN);
    }
}
