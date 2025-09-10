package com.minhhai.social_network.controller;

import com.minhhai.social_network.dto.response.ApiResponse.ApiSuccessResponse;
import com.minhhai.social_network.dto.response.ApiResponse.PageResponse;
import com.minhhai.social_network.dto.response.GroupMemberResponseDTO;
import com.minhhai.social_network.dto.response.JoinGroupRequestResponseDTO;
import com.minhhai.social_network.dto.response.PostResponseDTO;
import com.minhhai.social_network.service.GroupPostService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@Tag(name = "Group post controller")
@RequiredArgsConstructor
@Slf4j
public class GroupPostController {
    private final GroupPostService groupPostService;

    @GetMapping("/group/{groupId}/post")
    public ApiSuccessResponse<PageResponse<List<PostResponseDTO>>> getAllPostActiveWithFilter(
            @PathVariable @Min(value = 1, message = "Group id must be greater than 0") long groupId,
            Pageable pageable,
            @RequestParam(required = false) String... filters
    ) {
        return ApiSuccessResponse.<PageResponse<List<PostResponseDTO>>>builder()
                .data(groupPostService.getAllPostActiveWithFilter(groupId, pageable, filters))
                .message("Get all successfully!")
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/group/{groupId}/post/request")
    public ApiSuccessResponse<List<PostResponseDTO>> getAllCreatePostRequest(
            @PathVariable @Min(value = 1, message = "Group id must be greater than 0") long groupId
    ) {
        return ApiSuccessResponse.<List<PostResponseDTO>>builder()
                .data(groupPostService.getAllCreatePostRequest(groupId))
                .message("Get all successfully!")
                .status(HttpStatus.OK.value())
                .build();
    }

    @PostMapping("/group/post/{postId}/user-delete")
    public ApiSuccessResponse<PostResponseDTO> deleteMyPost(
            @PathVariable @Min(value = 1, message = "Post id must be greater than 0") long postId
    ) {
        return ApiSuccessResponse.<PostResponseDTO>builder()
                .data(groupPostService.deleteMyPost(postId))
                .message("Deleted successfully!")
                .status(HttpStatus.OK.value())
                .build();
    }

    @PostMapping("/group/post/{postId}/admin-delete")
    public ApiSuccessResponse<PostResponseDTO> adminDeletePost(
            @PathVariable @Min(value = 1, message = "Post id must be greater than 0") long postId
    ) {
        return ApiSuccessResponse.<PostResponseDTO>builder()
                .data(groupPostService.adminDeletePost(postId))
                .message("Deleted successfully!")
                .status(HttpStatus.OK.value())
                .build();
    }
}
