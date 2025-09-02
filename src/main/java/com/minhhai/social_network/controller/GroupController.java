package com.minhhai.social_network.controller;

import com.minhhai.social_network.dto.request.CreateGroupRequestDTO;
import com.minhhai.social_network.dto.request.LoginRequestDTO;
import com.minhhai.social_network.dto.response.ApiResponse.ApiSuccessResponse;
import com.minhhai.social_network.dto.response.GroupResponseDTO;
import com.minhhai.social_network.dto.response.JoinGroupRequestResponseDTO;
import com.minhhai.social_network.dto.response.TokenResponseDTO;
import com.minhhai.social_network.service.GroupService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/group/{groupId}/join-request")
    public ApiSuccessResponse<List<JoinGroupRequestResponseDTO>> getAllGroupJoinRequest(
            @PathVariable @Min(value = 1, message = "Group id must be greater than 0") long groupId
    ) {
        return ApiSuccessResponse.<List<JoinGroupRequestResponseDTO>>builder()
                .data(groupService.getAllGroupJoinRequest(groupId))
                .message("Get all successfully!")
                .status(HttpStatus.OK.value())
                .build();
    }
}
