package com.minhhai.social_network.util.enums;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    USER_EXISTED(409, "User existed", HttpStatus.CONFLICT),
    USER_NOT_EXISTED(401, "User not existed", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(401, "User unauthorized", HttpStatus.UNAUTHORIZED),
    OAUTH2_NOT_SUPPORTED(400, "Login not supported yet.", HttpStatus.BAD_REQUEST),
    ACCESS_DENIED(403, "Access denied", HttpStatus.FORBIDDEN),
    TOKEN_TYPE_INVALID(400, "Token type invalid", HttpStatus.BAD_REQUEST),
    JSON_INVALID(400, "JSON invalid", HttpStatus.BAD_REQUEST),
    ACCESS_TOKEN_INVALID(401, "Access token invalid", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_INVALID(401, "Refresh token invalid", HttpStatus.UNAUTHORIZED),
    ROLE_EXISTED(409, "Role existed", HttpStatus.CONFLICT),
    ROLE_NOT_EXISTED(400, "Role not existed", HttpStatus.BAD_REQUEST),
    PERMISSION_NOT_EXISTED(400, "Permission not existed", HttpStatus.BAD_REQUEST),
    GROUP_NOT_EXISTED(400, "Group does not existed", HttpStatus.BAD_REQUEST),
    REQUEST_NOT_EXISTED(400, "Request not existed", HttpStatus.BAD_REQUEST),
    COMMENT_NOT_EXISTED(400, "Comment not existed", HttpStatus.BAD_REQUEST),
    GROUP_MEMBER_EXISTED(400, "Group member existed", HttpStatus.BAD_REQUEST),
    GROUP_MEMBER_NOT_EXISTED(400, "Group member not existed", HttpStatus.BAD_REQUEST),
    REQUEST_PROCESSED(409, "Request processed", HttpStatus.BAD_REQUEST),
    INVALID_REQUEST(400, "Invalid request", HttpStatus.BAD_REQUEST),
    POST_NOT_EXISTED(400, "Post not existed", HttpStatus.BAD_REQUEST),
    USERNAME_EXISTED(409, "Username existed", HttpStatus.CONFLICT),
    EMAIL_EXISTED(409, "Email existed", HttpStatus.CONFLICT),
    RESOURCE_NOT_FOUND(404, "Resource not found", HttpStatus.NOT_FOUND),
    AUTHORITY_NOT_SUPPORTED(400, "Authority not supported", HttpStatus.BAD_REQUEST),
    TOKEN_SIGN_FAILED(500, "Token Sign failed", HttpStatus.INTERNAL_SERVER_ERROR),
    DESTINATION_INVALID(400, "Missing destination in SUBSCRIBE frame", HttpStatus.BAD_REQUEST),
    CONVERSATION_EXISTED(409, "Conversation existed", HttpStatus.CONFLICT),
    UPLOAD_FILE_FAILED(500, "Upload file failed", HttpStatus.INTERNAL_SERVER_ERROR),
    CONVERSATION_NOT_EXISTED(400, "Conversation does not exist", HttpStatus.BAD_REQUEST),
    USER_NOT_IN_CONVERSATION(400, "User not in conversation", HttpStatus.BAD_REQUEST),
    CONVERSATION_MEMBER_INVALID(400, "Conversation member invalid", HttpStatus.BAD_REQUEST),
    CONVERSATION_INVALID(400, "Conversation invalid", HttpStatus.BAD_REQUEST),
    OAUTH2_INVALID_PROVIDER(401, "OAuth2 provider invalid", HttpStatus.UNAUTHORIZED);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}