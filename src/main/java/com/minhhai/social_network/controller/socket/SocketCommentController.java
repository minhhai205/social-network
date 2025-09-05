package com.minhhai.social_network.controller.socket;

import com.minhhai.social_network.dto.request.CommentRequestDTO;
import com.minhhai.social_network.service.socket.SocketCommentService;
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
public class SocketCommentController {
    private final SocketCommentService socketCommentService;

    @MessageMapping("/comment/post/{postId}")
    public void createPostComment(
            @DestinationVariable @Min(value = 1, message = "Post id must be greater than 0") long postId,
            @Valid CommentRequestDTO commentRequestDTO,
            SimpMessageHeaderAccessor accessor
    ) {
        socketCommentService.createPostComment(postId, commentRequestDTO, accessor);
    }

}
