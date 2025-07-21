package com.minhhai.social_network.exception;

import com.minhhai.social_network.util.enums.ErrorCode;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class OAuth2Exception extends AuthException {

    public OAuth2Exception(ErrorCode errorCode) {
        super(errorCode);
    }
}
