package com.minhhai.social_network.service.socket;

import com.minhhai.social_network.dto.request.MessageRequestDTO;
import com.minhhai.social_network.entity.Message;
import com.minhhai.social_network.entity.MessageMedia;
import com.minhhai.social_network.service.FileService;
import com.minhhai.social_network.util.enums.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashSet;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebSocketService {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final FileService fileService;

    @Transactional
    public Message sendMessage(MessageRequestDTO request, Integer conversationId, SimpMessageHeaderAccessor accessor) {
//        if(request.getContent().isEmpty()) {
//            return null;
//        }

        Message message = new Message();

        if(request.getContent() != null) {
            message.setContent(request.getContent());
        }
        if(request.getMessageMedia() != null) {
            message.setMessageMedia(new HashSet<>());
            request.getMessageMedia().forEach(media -> {
                try {
                    String mediaUrl = fileService.upload(media, "social_network");

                    MessageMedia messageMedia = MessageMedia.builder()
                            .mediaUrl(mediaUrl)
                            .mediaType(media.getType().split("/")[0])
                            .build();

                    message.getMessageMedia().add(messageMedia);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        String username = accessor.getUser().getName();

        simpMessagingTemplate.convertAndSend("/topic/conversation/" + conversationId, message);
        simpMessagingTemplate.convertAndSendToUser(username, "/queue/chat", "data");

        return message;
    }


}
