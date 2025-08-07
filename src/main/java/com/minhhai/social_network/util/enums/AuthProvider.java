package com.minhhai.social_network.util.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum AuthProvider {
        GOOGLE,
        FACEBOOK,
        GITHUB,
        DEFAULT,
        UNKNOWN;

        @JsonCreator
        public static AuthProvider fromString(String value) {
                return EnumUtils.fromString(AuthProvider.class, value, UNKNOWN);
        }
}
