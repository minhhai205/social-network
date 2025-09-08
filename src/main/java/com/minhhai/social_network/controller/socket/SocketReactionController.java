package com.minhhai.social_network.controller.socket;

import com.minhhai.social_network.service.socket.SocketReactionService;
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
public class SocketReactionController {
    private final SocketReactionService socketReactionService;

    @MessageMapping("/reaction/post/{postId}")
    public void handlePostReaction(
            @DestinationVariable @Min(value = 1, message = "Post id must be greater than 0") long postId,
            SimpMessageHeaderAccessor accessor
    ) {
        socketReactionService.handlePostReaction(postId, accessor);
    }

        @MessageMapping("/reaction/comment/{commentId}")
    public void handleCommentReaction(
            @DestinationVariable @Min(value = 1, message = "Comment id must be greater than 0") long commentId,
            SimpMessageHeaderAccessor accessor
    ) {
        socketReactionService.handleCommentReaction(commentId, accessor);
    }
}