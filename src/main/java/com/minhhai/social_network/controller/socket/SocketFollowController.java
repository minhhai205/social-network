package com.minhhai.social_network.controller.socket;

import com.minhhai.social_network.service.socket.SocketFollowService;
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
public class SocketFollowController {
    private final SocketFollowService socketFollowService;

    @MessageMapping("/follow/user/{userId}")
    public void handleFollowToUser(
            @DestinationVariable @Min(value = 1, message = "User id must be greater than 0") long userId,
            SimpMessageHeaderAccessor accessor
    ) {
        socketFollowService.handleFollowToUser(userId, accessor);
    }
}
