package com.minhhai.social_network.controller;

import com.minhhai.social_network.dto.response.ApiResponse.ApiSuccessResponse;
import com.minhhai.social_network.dto.response.ApiResponse.PageResponse;
import com.minhhai.social_network.dto.response.PostResponseDTO;
import com.minhhai.social_network.service.UserPostService;
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
public class UserPostController {
    private final UserPostService userPostService;

    @GetMapping("/user/{userId}/post")
    public ApiSuccessResponse<PageResponse<List<PostResponseDTO>>> getAllPostActiveWithFilter(
            @PathVariable @Min(value = 1, message = "User id must be greater than 0") long userId,
            Pageable pageable,
            @RequestParam(required = false) String... filters
    ) {
        return ApiSuccessResponse.<PageResponse<List<PostResponseDTO>>>builder()
                .data(userPostService.getAllPostActiveWithFilter(userId, pageable, filters))
                .message("Get all successfully!")
                .status(HttpStatus.OK.value())
                .build();
    }

    @PostMapping("/user/post/{postId}/delete")
    public ApiSuccessResponse<PostResponseDTO> deleteMyPost(
            @PathVariable @Min(value = 1, message = "Post id must be greater than 0") long postId
    ) {
        return ApiSuccessResponse.<PostResponseDTO>builder()
                .data(userPostService.deleteMyPost(postId))
                .message("Deleted successfully!")
                .status(HttpStatus.OK.value())
                .build();
    }
}
