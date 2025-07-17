package com.minhhai.social_network.dto.response.ApiResponse;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Setter
@Getter
@SuperBuilder
public class ApiErrorResponse extends ApiResponse {
    private String error;
}