package com.minhhai.social_network.controller;

import com.minhhai.social_network.dto.response.ApiResponse.ApiSuccessResponse;
import com.minhhai.social_network.dto.response.UserDetailResponseDTO;
import com.minhhai.social_network.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@Tag(name = "User Controller")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping("/user/me")
    public ApiSuccessResponse<UserDetailResponseDTO> getMyInformation() {
        return ApiSuccessResponse.<UserDetailResponseDTO>builder()
                .data(userService.getMyInformation())
                .message("OK!")
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/user/{userId}/detail")
    public ApiSuccessResponse<UserDetailResponseDTO> getUserDetailInformation(
            @PathVariable @Min(value = 1, message = "User id must be greater than 0") long userId
    ) {
        return ApiSuccessResponse.<UserDetailResponseDTO>builder()
                .data(userService.getUserDetailInformation(userId))
                .message("OK!")
                .status(HttpStatus.OK.value())
                .build();
    }


}
