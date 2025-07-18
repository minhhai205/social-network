package com.minhhai.social_network.service;

import com.minhhai.social_network.entity.Token;
import com.minhhai.social_network.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    public void save(Token token) {
        tokenRepository.save(token);
    }

    public void deleteByJti(String jti) {
        tokenRepository.deleteById(jti);
    }

    public Optional<Token> findByJti(String jti) {
        return tokenRepository.findById(jti);
    }
}
