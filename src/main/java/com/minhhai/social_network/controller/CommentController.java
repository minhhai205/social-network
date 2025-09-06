package com.minhhai.social_network.controller;

import com.minhhai.social_network.dto.response.ApiResponse.ApiSuccessResponse;
import com.minhhai.social_network.dto.response.ApiResponse.PageResponse;
import com.minhhai.social_network.dto.response.CommentResponseDTO;
import com.minhhai.social_network.dto.response.GroupMemberResponseDTO;
import com.minhhai.social_network.service.CommentService;
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
@Tag(name = "Comment controller")
@RequiredArgsConstructor
@Slf4j
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/comment/post/{postId}")
    public ApiSuccessResponse<PageResponse<List<CommentResponseDTO>>> getAllCommentWithFilter(
            @PathVariable @Min(value = 1, message = "Post id must be greater than 0") long postId,
            Pageable pageable,
            @RequestParam(required = false) String... filters
    ) {
        return ApiSuccessResponse.<PageResponse<List<CommentResponseDTO>>>builder()
                .data(commentService.getAllCommentWithFilter(postId, pageable, filters))
                .message("Get all successfully!")
                .status(HttpStatus.OK.value())
                .build();
    }
}
