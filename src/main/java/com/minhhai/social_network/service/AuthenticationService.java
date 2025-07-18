package com.minhhai.social_network.service;

import com.minhhai.social_network.dto.request.LoginRequestDTO;
import com.minhhai.social_network.dto.response.TokenResponseDTO;
import com.minhhai.social_network.entity.Token;
import com.minhhai.social_network.entity.TokenResult;
import com.minhhai.social_network.entity.User;
import com.minhhai.social_network.exception.AppException;
import com.minhhai.social_network.repository.UserRepository;
import com.minhhai.social_network.util.enums.ErrorCode;
import com.minhhai.social_network.util.enums.TokenType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public TokenResponseDTO authenticate(LoginRequestDTO loginRequestDTO) {
        log.info("---------- authenticate login ----------");

        User user = userRepository.findByUsername(loginRequestDTO.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // create new access token
        TokenResult accessToken = jwtService.generateToken(user, TokenType.ACCESS_TOKEN);

        // create new refresh token
        TokenResult refreshToken = jwtService.generateToken(user, TokenType.REFRESH_TOKEN);

        // save refresh token to DB
        tokenService.save(Token.builder().jti(refreshToken.jti()).build());

        return TokenResponseDTO.builder()
                .accessToken(accessToken.token())
                .refreshToken(refreshToken.token())
                .build();
    }
}
