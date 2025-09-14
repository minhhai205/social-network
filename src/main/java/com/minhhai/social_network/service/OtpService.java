package com.minhhai.social_network.service;

import com.minhhai.social_network.util.enums.OtpType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {
    private final RedisService redisService;

    public String generateOtp(String email, OtpType otpType, Long expireSeconds) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        redisService.save(otpType.getValue() + email, otp, expireSeconds);
        return otp;
    }

    public boolean verifyOtp(String email, OtpType otpType, String otp) {
        String key = otpType.getValue() + email;
        Optional<String> savedOtp = redisService.get(key, String.class);
        if (savedOtp.isPresent() && savedOtp.get().equals(otp)) {
            redisService.delete(key);
            return true;
        }
        return false;
    }
}
