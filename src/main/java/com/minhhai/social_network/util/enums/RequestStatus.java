package com.minhhai.social_network.util.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum RequestStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    UNKNOWN;

    @JsonCreator
    public static RequestStatus fromString(String value) {
        return EnumUtils.fromString(RequestStatus.class, value, UNKNOWN);
    }
}
