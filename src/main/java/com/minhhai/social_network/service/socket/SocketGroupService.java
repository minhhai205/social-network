package com.minhhai.social_network.service.socket;

import com.minhhai.social_network.dto.response.GroupMemberResponseDTO;
import com.minhhai.social_network.entity.Group;
import com.minhhai.social_network.entity.GroupMember;
import com.minhhai.social_network.entity.Notification;
import com.minhhai.social_network.entity.User;
import com.minhhai.social_network.exception.AppException;
import com.minhhai.social_network.mapper.GroupMapper;
import com.minhhai.social_network.mapper.GroupMemberMapper;
import com.minhhai.social_network.mapper.NotificationMapper;
import com.minhhai.social_network.repository.GroupMemberRepository;
import com.minhhai.social_network.repository.GroupRepository;
import com.minhhai.social_network.repository.NotificationRepository;
import com.minhhai.social_network.repository.UserRepository;
import com.minhhai.social_network.util.commons.SecurityUtil;
import com.minhhai.social_network.util.enums.ErrorCode;
import com.minhhai.social_network.util.enums.GroupMemberStatus;
import com.minhhai.social_network.util.enums.GroupRole;
import com.minhhai.social_network.util.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void createJoinRequest(long groupId, SimpMessageHeaderAccessor accessor) {
        String currentUsername = accessor.getUser().getName();
        User curentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Group currentGroup = groupRepository.findByIdWithAndAllMember(groupId).orElseThrow(
                () -> new AppException(ErrorCode.GROUP_NOT_EXISTED));

        if(groupMemberRepository.findMemberByMemberId(curentUser.getId(), groupId).isPresent()) {
            throw new AppException(ErrorCode.GROUP_MEMBER_EXISTED);
        }

        GroupMember newMember = GroupMember.builder()
                .user(curentUser)
                .group(currentGroup)
                .role(GroupRole.MEMBER)
                .status(GroupMemberStatus.PENDING)
                .build();

        groupMemberRepository.save(newMember);

        // send notification to all admin
        currentGroup.getGroupMembers().stream()
                .filter(gm -> gm.getRole().equals(GroupRole.ADMIN))
                .map(GroupMember::getUser)
                .forEach(admin -> {

                    Notification notification = Notification.builder()
                            .content(curentUser.getFullName() + " created request to join group " + currentGroup.getName())
                            .type(NotificationType.GROUP_REQUEST)
                            .group(currentGroup)
                            .sendTo(admin)
                            .build();

                    notificationRepository.save(notification);

                    simpMessagingTemplate.convertAndSendToUser(admin.getUsername(),
                            "/queue/notifications", notificationMapper.toResponseDTO(notification));
                });

    }
}
