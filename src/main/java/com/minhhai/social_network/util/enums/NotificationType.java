package com.minhhai.social_network.util.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum NotificationType {
    MESSAGE,
    COMMENT,
    COMMENT_REPLY,
    POST,
    GROUP_POST,
    FOLLOW_REQUEST,
    FOLLOW_ACCEPT,
    GROUP_INVITE,
    GROUP_REQUEST,
    GROUP_ACCEPT,
    POST_LIKE,
    COMMENT_LIKE,
    UNKNOWN;

    @JsonCreator
    public static NotificationType fromString(String value) {
        return EnumUtils.fromString(NotificationType.class, value, UNKNOWN);
    }
}
