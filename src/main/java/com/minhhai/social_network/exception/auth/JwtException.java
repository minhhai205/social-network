package com.minhhai.social_network.exception.auth;

import com.minhhai.social_network.util.enums.ErrorCode;
import com.minhhai.social_network.util.enums.TokenType;
import lombok.Getter;

@Getter
public class JwtException extends AuthException {

    public JwtException(TokenType tokenType) {
        super(getErrorCodeByTokenType(tokenType));
    }

    private static ErrorCode getErrorCodeByTokenType(TokenType tokenType) {
        if (tokenType.equals(TokenType.ACCESS_TOKEN)) {
            return ErrorCode.ACCESS_TOKEN_INVALID;
        } else if (tokenType.equals(TokenType.REFRESH_TOKEN)) {
            return ErrorCode.REFRESH_TOKEN_INVALID;
        }
        return ErrorCode.UNAUTHORIZED;
    }
}