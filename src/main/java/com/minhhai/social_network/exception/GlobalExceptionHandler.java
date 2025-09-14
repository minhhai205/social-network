package com.minhhai.social_network.exception;

import com.minhhai.social_network.dto.response.ApiResponse.ApiErrorResponse;
import com.minhhai.social_network.dto.response.ApiResponse.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse MethodArgumentNotValidException(Exception e) {
        String message = e.getMessage();

        int start = message.lastIndexOf("[") + 1;
        int end = message.lastIndexOf("]") - 1;

        message = message.substring(start, end);

        return ApiErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .error("Invalid Payload")
                .build();
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            ConstraintViolationException.class,
            MethodArgumentTypeMismatchException.class,
            PropertyReferenceException.class
    })
    public ApiResponse handleHttpMessageNotReadableException(Exception e) {
        return ApiErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .error("Invalid Parameter")
                .build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ApiResponse handleAccessDeniedException() {
        return ApiErrorResponse.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .message("Access denied!")
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .build();
    }

    @ExceptionHandler(AppException.class)
    public ApiResponse handleAppException(AppException e) {
        return ApiErrorResponse.builder()
                .status(e.getErrorCode().getCode())
                .message(e.getErrorCode().getMessage())
                .error(e.getErrorCode().name())
                .build();
    }

    @ExceptionHandler(GeneralException.class)
    public ApiResponse handleGeneralException(GeneralException e) {
        return ApiErrorResponse.builder()
                .status(e.getCode())
                .message(e.getMessage())
                .error(e.getError())
                .build();
    }

//    @ExceptionHandler(Exception.class)
//    public ApiResponse handleException(Exception e) {
//        return ApiErrorResponse.builder()
//                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
//                .message(e.getMessage())
//                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
//                .build();
//    }
}


