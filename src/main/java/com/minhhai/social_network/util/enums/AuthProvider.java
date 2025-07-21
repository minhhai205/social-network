package com.minhhai.social_network.util.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AuthProvider {
        @JsonProperty("google")
        GOOGLE,
        @JsonProperty("facebook")
        FACEBOOK,
        @JsonProperty("github")
        GITHUB,
        DEFAULT
}
