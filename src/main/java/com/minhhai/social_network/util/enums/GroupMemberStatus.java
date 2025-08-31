package com.minhhai.social_network.util.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum GroupMemberStatus {
    ACTIVE,
    REMOVED,
    UNKNOWN;

    @JsonCreator
    public static GroupMemberStatus fromString(String value) {
        return EnumUtils.fromString(GroupMemberStatus.class, value, UNKNOWN);
    }
}
