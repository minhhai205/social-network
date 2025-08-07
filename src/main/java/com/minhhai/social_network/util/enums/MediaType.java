package com.minhhai.social_network.util.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum MediaType {
    IMAGE,
    VIDEO,
    LINK,
    UNKNOWN;

    @JsonCreator
    public static MediaType fromString(String value) {
        return EnumUtils.fromString(MediaType.class, value, UNKNOWN);
    }
}
