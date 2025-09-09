package com.minhhai.social_network.controller.socket;

import com.minhhai.social_network.dto.request.CreatePostRequestDTO;
import com.minhhai.social_network.service.socket.SocketUserPostService;
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
public class SocketUserPostController {
    private final SocketUserPostService socketUserPostService;

    @MessageMapping("/user/post/create")
    public void createPost(
            @Valid CreatePostRequestDTO createPostRequestDTO,
            SimpMessageHeaderAccessor accessor
    ) {
        socketUserPostService.createPost(createPostRequestDTO, accessor);
    }
}
