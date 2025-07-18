package com.minhhai.social_network.entity;

import lombok.*;

@Builder
public record TokenResult(String token, String jti) {
}
