package com.minhhai.social_network.config.securityCustom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minhhai.social_network.dto.response.ApiResponse.ApiErrorResponse;
import com.minhhai.social_network.exception.auth.AuthException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json;charset=UTF-8");

        ApiErrorResponse errorResponse;
        if(authException instanceof AuthException ex){
            errorResponse = ApiErrorResponse.builder()
                    .status(ex.getErrorCode().getCode())
                    .message(ex.getMessage())
                    .error(ex.getErrorCode().name())
                    .build();
        } else {
            errorResponse = ApiErrorResponse.builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message(authException.getMessage())
                    .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                    .build();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        response.flushBuffer();
    }
}