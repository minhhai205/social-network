package com.minhhai.social_network.controller.socket;

import com.minhhai.social_network.dto.request.CreateGroupRequestDTO;
import com.minhhai.social_network.dto.request.CreatePostRequestDTO;
import com.minhhai.social_network.service.socket.SocketGroupPostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@Slf4j
public class SocketGroupPostController {
    private final SocketGroupPostService socketGroupPostService;

    @MessageMapping("/group/{groupId}/post/create")
    public void createPostRequest(
            @DestinationVariable @Min(value = 1, message = "group id must be greater than 0") long groupId,
            @Valid CreatePostRequestDTO createPostRequestDTO,
            SimpMessageHeaderAccessor accessor
    ) {
        socketGroupPostService.createPostRequest(groupId, createPostRequestDTO, accessor);
    }

    @MessageMapping("/group/post/{postId}/approve")
    public void acceptJoinGroupRequest(
            @DestinationVariable @Min(value = 1, message = "Post id must be greater than 0") long postId,
            SimpMessageHeaderAccessor accessor
    ) {
        socketGroupPostService.acceptCreatePostRequest(postId, accessor);
    }

    @MessageMapping("/group/post/{postId}/reject")
    public void rejectJoinGroupRequest(
            @DestinationVariable @Min(value = 1, message = "Post id must be greater than 0") long postId,
            SimpMessageHeaderAccessor accessor
    ) {
        socketGroupPostService.rejectCreatePostRequest(postId, accessor);
    }
}
