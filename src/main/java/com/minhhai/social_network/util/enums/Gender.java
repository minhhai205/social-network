package com.minhhai.social_network.util.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Gender {
    MALE,
    FEMALE,
    OTHER,
    PREFER_NOT_TO_SAY,
    UNKNOWN;

    @JsonCreator
    public static Gender fromString(String value) {
        return EnumUtils.fromString(Gender.class, value, UNKNOWN);
    }
}
