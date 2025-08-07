package com.minhhai.social_network.util.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum Privacy {
    PUBLIC,
    PRIVATE,
    FRIENDS_ONLY,
    UNKNOWN;

    @JsonCreator
    public static Privacy fromString(String value) {
        return EnumUtils.fromString(Privacy.class, value, UNKNOWN);
    }
}
