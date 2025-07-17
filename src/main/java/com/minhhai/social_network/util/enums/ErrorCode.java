package com.minhhai.social_network.util.enums;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    USER_EXISTED(409, "User existed", HttpStatus.CONFLICT),
    USER_NOT_EXISTED(401, "User not existed", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(401, "User unauthorized", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED(403, "Access denied", HttpStatus.FORBIDDEN),
    TOKEN_TYPE_INVALID(400, "Token type invalid", HttpStatus.BAD_REQUEST),
    JSON_INVALID(400, "JSON invalid", HttpStatus.BAD_REQUEST),
    ACCESS_TOKEN_INVALID(401, "Access token invalid", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_INVALID(401, "Refresh token invalid", HttpStatus.UNAUTHORIZED),
    ROLE_EXISTED(409, "Role existed", HttpStatus.CONFLICT),
    ROLE_NOT_EXISTED(400, "Role not existed", HttpStatus.BAD_REQUEST),
    PERMISSION_NOT_EXISTED(400, "Permission not existed", HttpStatus.BAD_REQUEST),
    USERNAME_EXISTED(409, "Username existed", HttpStatus.CONFLICT),
    EMAIL_EXISTED(409, "Email existed", HttpStatus.CONFLICT),
    RESOURCE_NOT_FOUND(404, "Resource not found", HttpStatus.NOT_FOUND),
    AUTHORITY_NOT_SUPPORTED(400, "Authority not supported", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}