package com.minhhai.social_network.service;

import com.minhhai.social_network.dto.request.LoginRequestDTO;
import com.minhhai.social_network.dto.request.RegisterRequestDTO;
import com.minhhai.social_network.dto.response.TokenResponseDTO;
import com.minhhai.social_network.entity.Role;
import com.minhhai.social_network.entity.Token;
import com.minhhai.social_network.entity.TokenResult;
import com.minhhai.social_network.entity.User;
import com.minhhai.social_network.exception.AppException;
import com.minhhai.social_network.exception.auth.JwtException;
import com.minhhai.social_network.exception.auth.OAuth2Exception;
import com.minhhai.social_network.repository.RoleRepository;
import com.minhhai.social_network.repository.UserRepository;
import com.minhhai.social_network.util.commons.AppConst;
import com.minhhai.social_network.util.enums.AuthProvider;
import com.minhhai.social_network.util.enums.ErrorCode;
import com.minhhai.social_network.util.enums.Privacy;
import com.minhhai.social_network.util.enums.TokenType;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;
    private final RoleRepository roleRepository;

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

    public TokenResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        if(userRepository.existsByEmailAndDeletedFalse(registerRequestDTO.getEmail())
                ||userRepository.existsByUsernameAndDeletedFalse(registerRequestDTO.getUsername())){
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new OAuth2Exception(ErrorCode.ROLE_NOT_EXISTED));

        User newUser = User.builder()
                .fullName(registerRequestDTO.getFullName())
                .firstName(registerRequestDTO.getFirstName())
                .lastName(registerRequestDTO.getLastName())
                .username(registerRequestDTO.getUsername())
                .email(registerRequestDTO.getEmail())
                .password(passwordEncoder.encode(registerRequestDTO.getPassword()))
                .privacy(Privacy.PUBLIC)
                .gender(registerRequestDTO.getGender())
                .authProvider(AuthProvider.DEFAULT)
                .roles(Set.of(role))
                .build();

        userRepository.save(newUser);

        TokenResult accessToken = jwtService.generateToken(newUser, TokenType.ACCESS_TOKEN);
        TokenResult refreshToken = jwtService.generateToken(newUser, TokenType.REFRESH_TOKEN);

        TokenResponseDTO tokenResponse = TokenResponseDTO.builder()
                .accessToken(accessToken.token())
                .refreshToken(refreshToken.token())
                .build();

        tokenService.save(Token.builder().jti(refreshToken.jti()).build());

        return tokenResponse;
    }

    public TokenResponseDTO refresh(HttpServletRequest request){
        log.info("---------- refresh token ----------");

        try {
            final String refreshToken = request.getHeader("x-token");

            var signedJWT = jwtService.verifyToken(refreshToken, TokenType.REFRESH_TOKEN);
            String username = signedJWT.getJWTClaimsSet().getSubject();

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new JwtException(TokenType.REFRESH_TOKEN));

            TokenResult newAccessToken = jwtService.generateToken(user, TokenType.ACCESS_TOKEN);
            TokenResult newRefreshToken = jwtService.generateToken(user, TokenType.REFRESH_TOKEN);

            // delete old refresh token from DB and save new refresh token to DB
            tokenService.deleteByJti(signedJWT.getJWTClaimsSet().getJWTID());
            tokenService.save(Token.builder()
                    .jti(newRefreshToken.jti())
                    .build());

            return TokenResponseDTO.builder()
                    .accessToken(newAccessToken.token())
                    .refreshToken(newRefreshToken.token())
                    .build();
        } catch (ParseException e) {
            throw new JwtException(TokenType.REFRESH_TOKEN);
        }
    }

    public String logout(HttpServletRequest request) {
        try {
            String accessToken = request.getHeader("a-token");
            String refreshToken = request.getHeader("b-token");

            // validate token
            var signedAccessToken = jwtService.verifyToken(accessToken, TokenType.ACCESS_TOKEN);
            var signedRefreshToken = jwtService.verifyToken(refreshToken, TokenType.REFRESH_TOKEN);

            // add access token to black-list
            saveAccessTokenToBlackList(signedAccessToken);

            // delete refresh token from DB
            tokenService.deleteByJti(signedRefreshToken.getJWTClaimsSet().getJWTID());

            return "Logout successful!";
        } catch (ParseException e) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }


    private void saveAccessTokenToBlackList(SignedJWT signedAccessToken) throws ParseException {
        String jtiAccessToken = signedAccessToken.getJWTClaimsSet().getJWTID();
        Date expirationDate = signedAccessToken.getJWTClaimsSet().getExpirationTime();
        long timeout = (expirationDate.getTime() - new Date().getTime()) / 1000;
        redisService.save(AppConst.TOKEN_PREFIX + jtiAccessToken, "black_list", timeout);
    }
}
