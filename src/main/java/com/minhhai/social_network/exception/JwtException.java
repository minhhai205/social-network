package com.minhhai.social_network.exception;

import com.minhhai.social_network.util.enums.ErrorCode;
import com.minhhai.social_network.util.enums.TokenType;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class JwtException extends AuthenticationException {

    public JwtException(TokenType tokenType) {
        super(getMessageByTokenType(tokenType));
    }

    private static String getMessageByTokenType(TokenType tokenType) {
        if (tokenType.equals(TokenType.ACCESS_TOKEN)) {
            return ErrorCode.ACCESS_TOKEN_INVALID.getMessage();
        } else if (tokenType.equals(TokenType.REFRESH_TOKEN)) {
            return ErrorCode.REFRESH_TOKEN_INVALID.getMessage();
        }
        return ErrorCode.UNAUTHORIZED.getMessage();
    }
}