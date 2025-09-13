package com.minhhai.social_network.service.socket;

import com.minhhai.social_network.dto.request.GroupChatAddUserRequestDTO;
import com.minhhai.social_network.dto.request.CreateConversationRequestDTO;
import com.minhhai.social_network.dto.request.GroupChatDeleteUserRequestDTO;
import com.minhhai.social_network.dto.request.MessageRequestDTO;
import com.minhhai.social_network.dto.response.NotificationResponseDTO;
import com.minhhai.social_network.entity.*;
import com.minhhai.social_network.exception.AppException;
import com.minhhai.social_network.mapper.MessageMapper;
import com.minhhai.social_network.mapper.NotificationMapper;
import com.minhhai.social_network.repository.*;
import com.minhhai.social_network.service.FileService;
import com.minhhai.social_network.util.enums.ConversationRole;
import com.minhhai.social_network.util.enums.ErrorCode;
import com.minhhai.social_network.util.enums.NotificationType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.MessageFormat;
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
    private final NotificationRepository notificationRepository;
    private final ConversationMemberRepository conversationMemberRepository;

    @Transactional
    public void sendMessage(MessageRequestDTO request, long conversationId, SimpMessageHeaderAccessor accessor) {

        // check conversation
        String usernameSender = accessor.getUser().getName();
        Conversation conversation = getConversation(conversationId, false);

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

        // Gửi notification tới tất cả member trừ người gửi
        String content = userSent.getFullName() + " sent you a message.";
        sendNotificationToAllMember(conversation, usernameSender, content);

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
            String content = userCreated.getFullName() + " just added you to the chat group.";
            sendNotificationToAllMember(conversation, userCreated.getUsername(), content);
        }
    }

    private List<User> checkInvalidConversationAndGetMembers(CreateConversationRequestDTO createDTO, User userCreated){
        Set<Long> memberIds = new HashSet<>(createDTO.getMemberIds());
        memberIds.add(userCreated.getId());

        if (createDTO.isGroup()) {
            if (memberIds.size() <= 2) {
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

    private void sendNotificationToAllMember(Conversation conversation, String usernameSender, String content) {
        // Tạo list notification cho tất cả thành viên trừ sender
        List<Notification> notifications = conversation.getConversationMember().stream()
                .map(ConversationMember::getUser)
                .filter(member -> !member.getUsername().equals(usernameSender))
                .map(member -> Notification.builder()
                        .content(content)
                        .conversation(conversation)
                        .sendTo(member)
                        .type(NotificationType.MESSAGE)
                        .build())
                .toList();

        notificationRepository.saveAll(notifications);

        notifications.forEach(notification -> {
            NotificationResponseDTO dto = notificationMapper.toResponseDTO(notification);
            simpMessagingTemplate.convertAndSendToUser(
                    notification.getSendTo().getUsername(), "/queue/chat", dto);
        });
    }

    @Transactional
    public void addUserToGroupChat(@Valid GroupChatAddUserRequestDTO addUserRequest,
                                   SimpMessageHeaderAccessor accessor) {

        Conversation conversation = getConversation(addUserRequest.getConversationId(), true);

        // Tìm user tạo yêu cầu thêm thành viên
        String requesterUsername = accessor.getUser().getName();
        User addedBy = conversation.getConversationMember().stream()
                .map(ConversationMember::getUser)
                .filter(u -> u.getUsername().equals(requesterUsername))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.ACCESS_DENIED));

        Set<Long> existingMemberIds = conversation.getConversationMember().stream()
                .map(cm -> cm.getUser().getId())
                .collect(Collectors.toSet());

        addUserRequest.getMemberIds().stream()
                .filter(memberId -> !existingMemberIds.contains(memberId))
                .forEach(memberId -> {
                    Optional<ConversationMember> oldMemberOptional = conversationMemberRepository
                            .findOldMemberById(memberId, conversation.getId());

                    User newMember;

                    if (oldMemberOptional.isPresent()) {
                        ConversationMember member = oldMemberOptional.get();
                        member.setDeleted(false);
                        newMember = member.getUser();
                    } else{
                         newMember = userRepository.findById(memberId)
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

                        conversation.getConversationMember().add(
                                ConversationMember.builder()
                                        .user(newMember)
                                        .conversation(conversation)
                                        .role(ConversationRole.MEMBER)
                                        .build()
                        );
                    }

                    // Send notification
                    Notification notification = Notification.builder()
                            .content(addedBy.getFullName() + " just added you to the chat group.")
                            .sendTo(newMember)
                            .conversation(conversation)
                            .type(NotificationType.MESSAGE)
                            .build();
                    notificationRepository.save(notification);

                    NotificationResponseDTO notificationResponseDTO = notificationMapper.toResponseDTO(notification);

                    simpMessagingTemplate.convertAndSendToUser(
                            newMember.getUsername(), "/queue/chat", notificationResponseDTO);

                    // Send message
                    Message message = Message.builder()
                            .content(MessageFormat.format("{0} just added {1} to the chat group.",
                                    addedBy.getFullName(), newMember.getFullName()))
                            .conversation(conversation)
                            .build();

                    messageRepository.save(message);

                    simpMessagingTemplate.convertAndSend(
                            "/topic/conversation/" + conversation.getId(), messageMapper.toResponseDTO(message));
                });

        conversationRepository.save(conversation);
    }


    @Transactional
    public void deleteMemberFromGroup(@Valid GroupChatDeleteUserRequestDTO deleteUserRequest,
                                      SimpMessageHeaderAccessor accessor) {

        Conversation conversation = getConversation(deleteUserRequest.getConversationId(), true);

        String requesterUsername = accessor.getUser().getName();
        User deletedBy = conversation.getConversationMember().stream()
                .filter(cm -> cm.getRole() == ConversationRole.ADMIN
                        && cm.getUser().getUsername().equals(requesterUsername))
                .map(ConversationMember::getUser)
                .findAny().orElseThrow(() -> new AppException(ErrorCode.ACCESS_DENIED));

        ConversationMember member = conversation.getConversationMember().stream()
                .filter(cm -> cm.getUser().getId().equals(deleteUserRequest.getMemberId()))
                .findAny()
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_CONVERSATION));

        member.setDeleted(true);

        String content = MessageFormat.format("{0} just deleted {1} from the chat group.",
                deletedBy.getFullName(), member.getUser().getFullName());
        handleDeleteMember(conversation, content);

    }

    @Transactional
    public void leaveGroup(@Min(value = 1, message = "Group id must be greater than 0") long conversationId,
                           SimpMessageHeaderAccessor accessor) {

        Conversation conversation = getConversation(conversationId, true);

        String requesterUsername = accessor.getUser().getName();
        ConversationMember member = conversation.getConversationMember().stream()
                .filter(cm -> cm.getUser().getUsername().equals(requesterUsername))
                .findAny()
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_CONVERSATION));

        member.setDeleted(true);

        String content = member.getUser().getFullName() + " has left the group.";
        handleDeleteMember(conversation, content);
    }

    private void handleDeleteMember(Conversation conversation, String messageContent) {
        if (conversation.getConversationMember().size() - 1 <= 1) {
            conversation.setDeleted(true);
            conversationRepository.save(conversation);
        } else {
            Message message = Message.builder()
                    .content(messageContent)
                    .conversation(conversation)
                    .build();

            messageRepository.save(message);

            simpMessagingTemplate.convertAndSend(
                    "/topic/conversation/" + conversation.getId(), messageMapper.toResponseDTO(message));
        }
    }

    private Conversation getConversation(long conversationId, boolean isGroup) {
        Conversation conversation = conversationRepository
                .findByIdWithAllMember(conversationId)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_EXISTED));

        if (isGroup && !conversation.isGroup()) {
            throw new AppException(ErrorCode.CONVERSATION_INVALID);
        }
        return conversation;
    }
}
