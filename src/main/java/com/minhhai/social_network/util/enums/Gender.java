package com.minhhai.social_network.util.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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
