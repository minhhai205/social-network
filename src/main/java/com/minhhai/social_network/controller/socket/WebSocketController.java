package com.minhhai.social_network.controller.socket;

import com.minhhai.social_network.dto.request.MessageRequestDTO;
import com.minhhai.social_network.entity.Message;
import com.minhhai.social_network.service.socket.WebSocketService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@Tag(name = "Web socket Controller")
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {
    private final WebSocketService webSocketService;

    @MessageMapping("/message/{conversationId}")
    public Message sendMessage(@DestinationVariable Integer conversationId, MessageRequestDTO request, SimpMessageHeaderAccessor accessor){
        return webSocketService.sendMessage(request, conversationId, accessor);
    }
}
