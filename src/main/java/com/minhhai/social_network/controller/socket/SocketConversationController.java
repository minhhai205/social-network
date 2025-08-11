package com.minhhai.social_network.controller.socket;

import com.minhhai.social_network.dto.request.MessageRequestDTO;
import com.minhhai.social_network.entity.Message;
import com.minhhai.social_network.service.socket.SocketConversationService;
import com.minhhai.social_network.service.socket.WebSocketService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Socket conversation controller")
@RequiredArgsConstructor
@Slf4j
public class SocketConversationController {
    private final SocketConversationService socketConversationService;

    @MessageMapping("/message/{conversationId}")
    public void sendMessage(
            @DestinationVariable @Min(value = 1, message = "User id must be greater than 0") long conversationId,
            @Valid MessageRequestDTO request,
            SimpMessageHeaderAccessor accessor
    ){
        socketConversationService.sendMessage(request, conversationId, accessor);
    }
}
