package com.minhhai.social_network.service;

import com.minhhai.social_network.entity.TokenResult;
import com.minhhai.social_network.entity.User;
import com.minhhai.social_network.exception.AppException;
import com.minhhai.social_network.exception.auth.AuthException;
import com.minhhai.social_network.exception.auth.JwtException;
import com.minhhai.social_network.util.commons.AppConst;
import com.minhhai.social_network.util.enums.ErrorCode;
import com.minhhai.social_network.util.enums.TokenType;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${security.jwt.access.expired}")
    private long accessExpired;

    @Value("${security.jwt.refresh.expired}")
    private long refreshExpired;

    @Value("${security.jwt.access.key}")
    private String accessKey;

    @Value("${security.jwt.refresh.key}")
    private String refreshKey;

    private final TokenService tokenService;
    private final RedisService redisService;

    public TokenResult generateToken(User user, TokenType tokenType) {
        log.info("------------------------- generate token ------------------------------");

        long expiredTime = 1L;
        if (tokenType.equals(TokenType.ACCESS_TOKEN)) {
            expiredTime = 1000 * 60 * 60 * accessExpired;
        } else if (tokenType.equals(TokenType.REFRESH_TOKEN)) {
            expiredTime = 1000 * 60 * 60 * 24 * refreshExpired;
        }

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        String jti = UUID.randomUUID().toString();

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("minhhai.com")
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + expiredTime))
                .jwtID(jti)
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(getSignerKey(tokenType).getBytes()));
            return TokenResult.builder().token(jwsObject.serialize()).jti(jti).build();
        } catch (JOSEException e) {
            log.error("-------------- Cannot create token --------------", e);
            throw new AuthException(ErrorCode.TOKEN_SIGN_FAILED);
        }
    }

    public SignedJWT verifyToken(String token, TokenType tokenType) {
        try {
            JWSVerifier verifier = new MACVerifier(getSignerKey(tokenType).getBytes());
            SignedJWT signedJWT = SignedJWT.parse(token);

            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

            var verified = signedJWT.verify(verifier);

            if (!(verified && expiryTime.after(new Date()))) throw new JwtException(tokenType);

            checkTokenInDB(signedJWT.getJWTClaimsSet().getJWTID(), tokenType);
            return signedJWT;
        } catch (JOSEException | ParseException e) {
            throw new JwtException(tokenType);
        }
    }


    public void checkTokenInDB(String jti, TokenType tokenType) {
        if (tokenType.equals(TokenType.ACCESS_TOKEN)) {
            // check access token in black-list.....
//            Optional<String> jtiRedis = redisService.get(AppConst.TOKEN_PREFIX + jti, String.class);
//            if (jtiRedis.isPresent()) {
//                throw new JwtException(TokenType.ACCESS_TOKEN);
//            }
            return;
        } else if (tokenType.equals(TokenType.REFRESH_TOKEN)) {
            tokenService.findByJti(jti)
                    .orElseThrow(() -> new JwtException(tokenType));
        }
    }

    private String getSignerKey(TokenType tokenType) {
        if (tokenType.equals(TokenType.ACCESS_TOKEN)) {
            return accessKey;
        } else if (tokenType.equals(TokenType.REFRESH_TOKEN)) {
            return refreshKey;
        } else {
            throw new AppException(ErrorCode.TOKEN_TYPE_INVALID);
        }
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
            });

        return stringJoiner.toString();
    }
}
