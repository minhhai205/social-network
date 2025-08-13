package com.minhhai.social_network.service.socket;

import com.minhhai.social_network.dto.request.CreateConversationRequestDTO;
import com.minhhai.social_network.dto.request.MessageRequestDTO;
import com.minhhai.social_network.dto.response.NotificationResponseDTO;
import com.minhhai.social_network.entity.*;
import com.minhhai.social_network.exception.AppException;
import com.minhhai.social_network.mapper.MessageMapper;
import com.minhhai.social_network.mapper.NotificationMapper;
import com.minhhai.social_network.repository.ConversationRepository;
import com.minhhai.social_network.repository.MessageRepository;
import com.minhhai.social_network.repository.UserRepository;
import com.minhhai.social_network.service.FileService;
import com.minhhai.social_network.util.enums.ConversationRole;
import com.minhhai.social_network.util.enums.ErrorCode;
import com.minhhai.social_network.util.enums.NotificationType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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

        NotificationResponseDTO notificationResponseDTO = notificationMapper.toResponseDTO(notification);

        // Gửi notification tới tất cả member trừ người gửi
        conversation.getConversationMember().stream()
                .map(cm -> cm.getUser().getUsername())
                .filter(usernameMember -> !usernameMember.equals(usernameSender))
                .forEach(usernameMember -> {
                    simpMessagingTemplate.convertAndSendToUser(
                            usernameMember, "/queue/chat", notificationResponseDTO);
                });

    }

    @Transactional
    public void createConversation(CreateConversationRequestDTO createDTO, SimpMessageHeaderAccessor accessor) {
        User userCreated = userRepository.findByUsername(accessor.getUser().getName()).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Conversation conversation = Conversation.builder()
                .name(createDTO.isGroup() ? createDTO.getName() : null)
                .createdBy(userCreated)
                .isGroup(createDTO.isGroup())
                .build();

        List<User> members = checkInvalidConversationAndGetMembers(createDTO, userCreated);

        Set<ConversationMember> conversationMembers = members.stream()
                .map(member -> ConversationMember.builder()
                        .user(member)
                        .conversation(conversation)
                        .role(member.getId().equals(userCreated.getId())
                                ? ConversationRole.ADMIN
                                : ConversationRole.MEMBER)
                        .build())
                .collect(Collectors.toSet());

        conversation.setConversationMember(conversationMembers);

        conversationRepository.save(conversation);

        if(createDTO.isGroup()) {
            Notification notification = Notification.builder()
                    .content(userCreated.getFullName() + " just added you to the chat group.")
                    .type(NotificationType.MESSAGE)
                    .build();

            NotificationResponseDTO notificationResponseDTO = notificationMapper.toResponseDTO(notification);

            conversationMembers.stream()
                    .map(cm -> cm.getUser().getUsername())
                    .filter(username -> !username.equals(userCreated.getUsername()))
                    .forEach(username -> {
                        simpMessagingTemplate.convertAndSendToUser(
                                username, "/queue/chat", notificationResponseDTO);
                    });
        }
    }

    private List<User> checkInvalidConversationAndGetMembers(CreateConversationRequestDTO createDTO, User userCreated){
        Set<Long> memberIds = new HashSet<>(createDTO.getMemberIds());
        memberIds.add(userCreated.getId());

        if (createDTO.isGroup()) {
            if (memberIds.size() < 2) {
                throw new AppException(ErrorCode.CONVERSATION_MEMBER_INVALID);
            }
        } else {
            if (memberIds.size() != 2) {
                throw new AppException(ErrorCode.CONVERSATION_MEMBER_INVALID);
            }
            checkConversationOneToOneExisted(memberIds);
        }

        List<User> members = userRepository.findByIdIn(memberIds);
        if(members.size() != memberIds.size()) {
            throw new AppException(ErrorCode.CONVERSATION_MEMBER_INVALID);
        }

        return members;
    }

    private void checkConversationOneToOneExisted(Set<Long> memberIds){
        List<Long> sortedIds = new ArrayList<>(memberIds);
        sortedIds.sort(Long::compareTo);

        Optional<Conversation> existing = conversationRepository.findOneToOneConversation(sortedIds.get(0), sortedIds.get(1));
        if (existing.isPresent()) {
            throw new AppException(ErrorCode.CONVERSATION_EXISTED);
        }
    }
}
