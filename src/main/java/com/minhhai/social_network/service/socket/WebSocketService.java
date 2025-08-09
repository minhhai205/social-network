package com.minhhai.social_network.service.socket;

import com.minhhai.social_network.dto.request.MessageRequestDTO;
import com.minhhai.social_network.entity.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebSocketService {
    private final SimpMessagingTemplate simpMessagingTemplate;


    @Transactional
    public Message sendMessage(MessageRequestDTO request, Integer conversationId, SimpMessageHeaderAccessor accessor) {
//        if(request.getContent().isEmpty()) {
//            return null;
//        }

        Message message = Message.builder()
                .content("request.getContent()")
                .build();

        String username = accessor.getUser().getName();

        simpMessagingTemplate.convertAndSend("/topic/conversation/" + conversationId, message);
        simpMessagingTemplate.convertAndSendToUser(username, "/queue/chat", "data");

        return message;
    }


}
