package com.minhhai.social_network.util.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ConversationRole {
    MEMBER,
    ADMIN,
    UNKNOWN;

    @JsonCreator
    public static ConversationRole fromString(String value) {
        return EnumUtils.fromString(ConversationRole.class, value, UNKNOWN);
    }
}
