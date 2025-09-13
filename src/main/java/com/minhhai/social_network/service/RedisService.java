package com.minhhai.social_network.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void save(String key, Object value, long expirationSeconds) {
        if (expirationSeconds <= 0) {
            throw new IllegalArgumentException("TTL must be > 0");
        }
        redisTemplate.opsForValue().set(key, value, expirationSeconds, TimeUnit.SECONDS);
    }

    public <T> Optional<T> get(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) return Optional.empty();
        try {
            return Optional.of(objectMapper.convertValue(value, clazz));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }
}

