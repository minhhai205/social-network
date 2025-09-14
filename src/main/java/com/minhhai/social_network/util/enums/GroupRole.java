package com.minhhai.social_network.util.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum GroupRole {
    MEMBER,
    ADMIN,
    MODERATOR,
    UNKNOWN;

    @JsonCreator
    public static GroupRole fromString(String value) {
        return EnumUtils.fromString(GroupRole.class, value, UNKNOWN);
    }
}
