package com.minhhai.social_network.controller;

import com.minhhai.social_network.dto.request.LoginRequestDTO;
import com.minhhai.social_network.dto.request.RegisterRequestDTO;
import com.minhhai.social_network.dto.request.VerifyRegisterRequestDTO;
import com.minhhai.social_network.dto.response.ApiResponse.ApiSuccessResponse;
import com.minhhai.social_network.dto.response.RegisterResponseDTO;
import com.minhhai.social_network.dto.response.TokenResponseDTO;
import com.minhhai.social_network.service.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@Tag(name = "Auth Controller")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/auth/login")
    public ApiSuccessResponse<TokenResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return ApiSuccessResponse.<TokenResponseDTO>builder()
                .data(authenticationService.authenticate(loginRequestDTO))
                .message("Authenticated!")
                .status(HttpStatus.OK.value())
                .build();
    }

    @PostMapping("/auth/register")
    public ApiSuccessResponse<RegisterResponseDTO> register(
            @Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        return ApiSuccessResponse.<RegisterResponseDTO>builder()
                .data(authenticationService.register(registerRequestDTO))
                .message("Registration request successful!")
                .status(HttpStatus.OK.value())
                .build();
    }

    @PostMapping("/auth/verify_register")
    public ApiSuccessResponse<TokenResponseDTO> verifyRegister(
            @Valid @RequestBody VerifyRegisterRequestDTO verifyRegisterRequestDTO) {
        return ApiSuccessResponse.<TokenResponseDTO>builder()
                .data(authenticationService.verifyRegister(verifyRegisterRequestDTO))
                .message("Registered successfully!")
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("api/test")
    public ApiSuccessResponse<String> test() {
        return ApiSuccessResponse.<String>builder()
                .data("okkkkkkk")
                .message("Authenticated!")
                .status(HttpStatus.OK.value())
                .build();
    }

    @PostMapping("/auth/refresh")
    public ApiSuccessResponse<TokenResponseDTO> refresh(HttpServletRequest request) {
        return ApiSuccessResponse.<TokenResponseDTO>builder()
                .data(authenticationService.refresh(request))
                .message("Refreshed!")
                .status(HttpStatus.OK.value())
                .build();
    }

    @PostMapping("/auth/logout")
    public ApiSuccessResponse<String> logout(HttpServletRequest request) {
        return ApiSuccessResponse.<String>builder()
                .data(authenticationService.logout(request))
                .message("Logged out!")
                .status(HttpStatus.OK.value())
                .build();
    }

}
