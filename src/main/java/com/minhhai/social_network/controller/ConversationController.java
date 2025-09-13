package com.minhhai.social_network.controller;

import com.minhhai.social_network.dto.response.ApiResponse.ApiSuccessResponse;
import com.minhhai.social_network.dto.response.ApiResponse.PageResponse;
import com.minhhai.social_network.dto.response.ConversationResponseDTO;
import com.minhhai.social_network.dto.response.PostResponseDTO;
import com.minhhai.social_network.service.ConversationService;
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
@Tag(name = "Conversation controller")
@RequiredArgsConstructor
@Slf4j
public class ConversationController {
    private final ConversationService conversationService;

    @GetMapping("/conversation/my-conversation")
    public ApiSuccessResponse<PageResponse<List<ConversationResponseDTO>>> getAllMyConversationWithFilter(
            Pageable pageable,
            @RequestParam(required = false) String... filters
    ) {
        return ApiSuccessResponse.<PageResponse<List<ConversationResponseDTO>>>builder()
                .data(conversationService.getAllMyConversationWithFilter(pageable, filters))
                .message("Get all successfully!")
                .status(HttpStatus.OK.value())
                .build();
    }
}
