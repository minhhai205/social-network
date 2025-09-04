package com.minhhai.social_network.controller;

import com.minhhai.social_network.dto.response.ApiResponse.ApiSuccessResponse;
import com.minhhai.social_network.dto.response.JoinGroupRequestResponseDTO;
import com.minhhai.social_network.dto.response.PostResponseDTO;
import com.minhhai.social_network.service.GroupPostService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@Tag(name = "Group post controller")
@RequiredArgsConstructor
@Slf4j
public class GroupPostController {
    private final GroupPostService groupPostService;

    @GetMapping("/group/{groupId}/post")
    public ApiSuccessResponse<List<PostResponseDTO>> getAllCreatePostRequest(
            @PathVariable @Min(value = 1, message = "Group id must be greater than 0") long groupId
    ) {
        return ApiSuccessResponse.<List<PostResponseDTO>>builder()
                .data(groupPostService.getAllCreatePostRequest(groupId))
                .message("Get all successfully!")
                .status(HttpStatus.OK.value())
                .build();
    }
}
