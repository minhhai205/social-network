package com.minhhai.social_network.service.socket;

import com.minhhai.social_network.dto.request.MessageRequestDTO;
import com.minhhai.social_network.entity.*;
import com.minhhai.social_network.exception.AppException;
import com.minhhai.social_network.mapper.MessageMapper;
import com.minhhai.social_network.mapper.NotificationMapper;
import com.minhhai.social_network.repository.ConversationRepository;
import com.minhhai.social_network.repository.MessageRepository;
import com.minhhai.social_network.repository.UserRepository;
import com.minhhai.social_network.service.FileService;
import com.minhhai.social_network.util.enums.ErrorCode;
import com.minhhai.social_network.util.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashSet;

@Service
@Slf4j
@RequiredArgsConstructor
public class SocketConversationService {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final FileService fileService;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final ConversationRepository conversationRepository;
    private final NotificationMapper notificationMapper;

    @Transactional
    public void sendMessage(MessageRequestDTO request, long conversationId, SimpMessageHeaderAccessor accessor) {

        // check conversation
        String usernameSender = accessor.getUser().getName();
        Conversation conversation = conversationRepository.findByIdWithUserCreatedAndAllMember(conversationId)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_EXISTED));

        // Tìm userSent từ danh sách members đã load
        User userSent = conversation.getConversationMember().stream()
                .map(ConversationMember::getUser)
                .filter(u -> u.getUsername().equals(usernameSender))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_CONVERSATION));

        Message message = Message.builder()
                .userSent(userSent)
                .conversation(conversation)
                .build();

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
                            .message(message)
                            .build();

                    message.getMessageMedia().add(messageMedia);
                } catch (IOException e) {
                    throw new AppException(ErrorCode.UPLOAD_FILE_FAILED);
                }
            });
        }

        messageRepository.save(message);

        simpMessagingTemplate.convertAndSend(
                "/topic/conversation/" + conversationId, messageMapper.toResponseDTO(message));

        Notification notification = Notification.builder()
                .content(userSent.getFullName() + " sent you a message.")
                .type(NotificationType.MESSAGE)
                .build();

        // Gửi notification tới tất cả member trừ người gửi
        conversation.getConversationMember().stream()
                .map(cm -> cm.getUser().getUsername())
                .filter(usernameMember -> !usernameMember.equals(usernameSender))
                .forEach(usernameMember -> {
                    simpMessagingTemplate.convertAndSendToUser(
                            usernameMember, "/queue/chat", notificationMapper.toResponseDTO(notification));
                });

    }
}
