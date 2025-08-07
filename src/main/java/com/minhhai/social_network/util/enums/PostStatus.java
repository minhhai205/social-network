package com.minhhai.social_network.util.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum PostStatus {
    PENDING,
    APPROVED,
    REJECTED,
    REMOVED,
    UNKNOWN;

    @JsonCreator
    public static PostStatus fromString(String value) {
        return EnumUtils.fromString(PostStatus.class, value, UNKNOWN);
    }
}
