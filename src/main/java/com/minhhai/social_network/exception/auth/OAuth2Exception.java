package com.minhhai.social_network.exception.auth;

import com.minhhai.social_network.util.enums.ErrorCode;
import lombok.Getter;

@Getter
public class OAuth2Exception extends AuthException {

    public OAuth2Exception(ErrorCode errorCode) {
        super(errorCode);
    }
}
