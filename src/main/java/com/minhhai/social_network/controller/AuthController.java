package com.minhhai.social_network.controller;

import com.minhhai.social_network.dto.response.ApiResponse.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@Tag(name = "Auth Controller")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    @GetMapping("api/auth/test")
    public ApiSuccessResponse<String> test() {
        return ApiSuccessResponse.<String>builder()
                .data("okkkkkkk")
                .message("Authenticated!")
                .status(HttpStatus.OK.value())
                .build();
    }

}
