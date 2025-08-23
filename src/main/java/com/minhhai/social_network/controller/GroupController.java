package com.minhhai.social_network.controller;

import com.minhhai.social_network.dto.request.CreateGroupRequestDTO;
import com.minhhai.social_network.dto.request.LoginRequestDTO;
import com.minhhai.social_network.dto.response.ApiResponse.ApiSuccessResponse;
import com.minhhai.social_network.dto.response.GroupResponseDTO;
import com.minhhai.social_network.dto.response.TokenResponseDTO;
import com.minhhai.social_network.service.GroupService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@Tag(name = "Group controller")
@RequiredArgsConstructor
@Slf4j
public class GroupController {
    private final GroupService groupService;

    @PostMapping("/group/create")
    public ApiSuccessResponse<GroupResponseDTO> createNewGroup(
            @Valid @RequestBody CreateGroupRequestDTO createGroupRequestDTO
    ) {
        return ApiSuccessResponse.<GroupResponseDTO>builder()
                .data(groupService.createNewGroup(createGroupRequestDTO))
                .message("Created successfully!")
                .status(HttpStatus.OK.value())
                .build();
    }
}
