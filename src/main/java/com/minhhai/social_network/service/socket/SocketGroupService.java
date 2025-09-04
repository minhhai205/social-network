package com.minhhai.social_network.service.socket;

import com.minhhai.social_network.dto.response.GroupMemberResponseDTO;
import com.minhhai.social_network.entity.*;
import com.minhhai.social_network.exception.AppException;
import com.minhhai.social_network.mapper.GroupMapper;
import com.minhhai.social_network.mapper.GroupMemberMapper;
import com.minhhai.social_network.mapper.NotificationMapper;
import com.minhhai.social_network.repository.*;
import com.minhhai.social_network.util.commons.SecurityUtil;
import com.minhhai.social_network.util.enums.*;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SocketGroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMemberMapper groupMemberMapper;
    private final GroupMemberRepository groupMemberRepository;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final NotificationMapper notificationMapper;
    private final JoinGroupRequestRepository joinGroupRequestRepository;

    @Transactional
    public void createJoinRequest(long groupId, SimpMessageHeaderAccessor accessor) {
        String currentUsername = accessor.getUser().getName();
        User curentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Group currentGroup = groupRepository.findById(groupId).orElseThrow(
                () -> new AppException(ErrorCode.GROUP_NOT_EXISTED));

        if(groupMemberRepository.findMemberByMemberId(curentUser.getId(), groupId).isPresent()) {
            throw new AppException(ErrorCode.GROUP_MEMBER_EXISTED);
        }

        JoinGroupRequest request = JoinGroupRequest.builder()
                .createdBy(curentUser)
                .group(currentGroup)
                .status(RequestStatus.PENDING)
                .build();

        joinGroupRequestRepository.save(request);

        // send notification to all admin and moderator
        List<GroupMember> adminsAndModerators = groupMemberRepository.findAllAdminAndModeratorByGroupId(groupId);

        adminsAndModerators.forEach(admin -> {
            Notification notification = Notification.builder()
                    .content(curentUser.getFullName() + " created request to join group " + currentGroup.getName())
                    .type(NotificationType.GROUP_REQUEST)
                    .group(currentGroup)
                    .sendTo(admin.getUser())
                    .build();

            notificationRepository.save(notification);

            simpMessagingTemplate.convertAndSendToUser(admin.getUser().getUsername(),
                    "/queue/notifications", notificationMapper.toResponseDTO(notification));
        });
    }

    @Transactional
    public void acceptJoinGroupRequest(long requestId, SimpMessageHeaderAccessor accessor) {
        JoinGroupRequest request = verifyAndGetRequest(requestId, accessor);

        request.setStatus(RequestStatus.ACCEPTED);
        joinGroupRequestRepository.save(request);

        // Nếu member đã từng tham gia và rời nhóm thì chỉ xét lại status
        Optional<GroupMember> oldMemberOptional = groupMemberRepository.findGroupMemberRemovedById(
                request.getCreatedBy().getId(), request.getGroup().getId());

        if(oldMemberOptional.isPresent()) {
            GroupMember oldMember = oldMemberOptional.get();
            oldMember.setStatus(GroupMemberStatus.ACTIVE);
            groupMemberRepository.save(oldMember);
        } else {
            GroupMember newGroupMember = GroupMember.builder()
                    .group(request.getGroup())
                    .user(request.getCreatedBy())
                    .role(GroupRole.MEMBER)
                    .status(GroupMemberStatus.ACTIVE)
                    .build();
            groupMemberRepository.save(newGroupMember);
        }

        Notification notification = Notification.builder()
                .content("You have been approved for the group " + request.getGroup().getName())
                .sendTo(request.getCreatedBy())
                .type(NotificationType.GROUP_ACCEPT)
                .group(request.getGroup())
                .build();
        notificationRepository.save(notification);

        simpMessagingTemplate.convertAndSendToUser(request.getCreatedBy().getUsername(),
                "/queue/notifications", notificationMapper.toResponseDTO(notification));
    }

    @Transactional
    public void rejectJoinGroupRequest(long requestId, SimpMessageHeaderAccessor accessor) {
        JoinGroupRequest request = verifyAndGetRequest(requestId, accessor);

        request.setStatus(RequestStatus.REJECTED);
        joinGroupRequestRepository.save(request);

        Notification notification = Notification.builder()
                .content("You have been rejected for the group " + request.getGroup().getName())
                .sendTo(request.getCreatedBy())
                .type(NotificationType.GROUP_ACCEPT)
                .group(request.getGroup())
                .build();
        notificationRepository.save(notification);

        simpMessagingTemplate.convertAndSendToUser(request.getCreatedBy().getUsername(),
                "/queue/notifications", notificationMapper.toResponseDTO(notification));
    }

    private JoinGroupRequest verifyAndGetRequest(Long requestId, SimpMessageHeaderAccessor accessor) {
        User currentUser = userRepository.findByUsername(accessor.getUser().getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        JoinGroupRequest request = joinGroupRequestRepository.findByIdWithGroupAndUserCreated(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOT_EXISTED));

        groupMemberRepository.findAdminOrModeratorById(currentUser.getId(), request.getGroup().getId())
                .orElseThrow(() -> new AppException(ErrorCode.ACCESS_DENIED));

        return request;
    }
}
