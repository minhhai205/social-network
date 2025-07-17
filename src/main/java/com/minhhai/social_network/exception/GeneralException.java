package com.minhhai.social_network.exception;

import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException {
    private final int code;
    private final String error;

    public GeneralException(int code, String message, String error) {
        super(message);
        this.code = code;
        this.error = error;
    }
}
