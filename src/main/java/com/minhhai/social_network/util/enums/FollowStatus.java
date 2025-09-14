package com.minhhai.social_network.util.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum FollowStatus {
    PENDING,
    ACCEPTED,
    DECLINED,
    UNKNOWN;

    @JsonCreator
    public static FollowStatus fromString(String value) {
        return EnumUtils.fromString(FollowStatus.class, value, UNKNOWN);
    }
}
