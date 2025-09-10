package com.minhhai.social_network.controller;

import com.minhhai.social_network.dto.response.ApiResponse.ApiSuccessResponse;
import com.minhhai.social_network.dto.response.ApiResponse.PageResponse;
import com.minhhai.social_network.dto.response.CommentResponseDTO;
import com.minhhai.social_network.dto.response.ReactionResponseDTO;
import com.minhhai.social_network.service.ReactionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@Tag(name = "Reaction post controller")
@RequiredArgsConstructor
@Slf4j
public class ReactionController {
    private final ReactionService reactionService;

    @GetMapping("/reaction/post/{postId}")
    public ApiSuccessResponse<PageResponse<List<ReactionResponseDTO>>> getAllPostReactionWithFilter(
            @PathVariable @Min(value = 1, message = "Post id must be greater than 0") long postId,
            Pageable pageable,
            @RequestParam(required = false) String... filters
    ) {
        return ApiSuccessResponse.<PageResponse<List<ReactionResponseDTO>>>builder()
                .data(reactionService.getAllPostReactionWithFilter(postId, pageable, filters))
                .message("Get all successfully!")
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/reaction/comment/{commentId}")
    public ApiSuccessResponse<PageResponse<List<ReactionResponseDTO>>> getAllCommentReactionWithFilter(
            @PathVariable @Min(value = 1, message = "Comment id must be greater than 0") long commentId,
            Pageable pageable,
            @RequestParam(required = false) String... filters
    ) {
        return ApiSuccessResponse.<PageResponse<List<ReactionResponseDTO>>>builder()
                .data(reactionService.getAllCommentReactionWithFilter(commentId, pageable, filters))
                .message("Get all successfully!")
                .status(HttpStatus.OK.value())
                .build();
    }
}
