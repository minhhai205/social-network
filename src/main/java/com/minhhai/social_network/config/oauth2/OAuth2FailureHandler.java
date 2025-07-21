package com.minhhai.social_network.config.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minhhai.social_network.dto.response.ApiResponse.ApiErrorResponse;
import com.minhhai.social_network.exception.AuthException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2FailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        ApiErrorResponse errorResponse;
        if(exception instanceof AuthException ex){
            errorResponse = ApiErrorResponse.builder()
                    .status(ex.getErrorCode().getCode())
                    .message(exception.getMessage())
                    .error(ex.getErrorCode().name())
                    .build();
        } else {
            errorResponse = ApiErrorResponse.builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message(exception.getMessage())
                    .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                    .build();
        }

        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getWriter(), errorResponse);

    }
}
