package com.minhhai.social_network.config.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minhhai.social_network.config.security.securityModel.SecurityUser;
import com.minhhai.social_network.dto.response.ApiResponse.ApiSuccessResponse;
import com.minhhai.social_network.dto.response.TokenResponseDTO;
import com.minhhai.social_network.entity.Token;
import com.minhhai.social_network.entity.TokenResult;
import com.minhhai.social_network.service.JwtService;
import com.minhhai.social_network.service.TokenService;
import com.minhhai.social_network.util.enums.TokenType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class Oauth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final TokenService tokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        SecurityUser user = (SecurityUser) authentication.getPrincipal();

        TokenResult accessToken = jwtService.generateToken(user.getUser(), TokenType.ACCESS_TOKEN);
        TokenResult refreshToken = jwtService.generateToken(user.getUser(), TokenType.REFRESH_TOKEN);

        TokenResponseDTO tokenResponse = TokenResponseDTO.builder()
                .accessToken(accessToken.token())
                .refreshToken(refreshToken.token())
                .build();

        tokenService.save(Token.builder().jti(refreshToken.jti()).build());

        ApiSuccessResponse<TokenResponseDTO> successResponse = ApiSuccessResponse.<TokenResponseDTO>builder()
                .data(tokenResponse)
                .status(HttpStatus.OK.value())
                .message("Authenticated!")
                .build();

        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getWriter(), successResponse);
    }
}
