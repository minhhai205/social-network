package com.minhhai.social_network.service.socket;

import com.minhhai.social_network.entity.Follow;
import com.minhhai.social_network.entity.Notification;
import com.minhhai.social_network.entity.User;
import com.minhhai.social_network.exception.AppException;
import com.minhhai.social_network.mapper.NotificationMapper;
import com.minhhai.social_network.repository.FollowRepository;
import com.minhhai.social_network.repository.NotificationRepository;
import com.minhhai.social_network.repository.UserRepository;
import com.minhhai.social_network.util.enums.ErrorCode;
import com.minhhai.social_network.util.enums.FollowStatus;
import com.minhhai.social_network.util.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SocketFollowService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final NotificationMapper notificationMapper;

    public void handleFollowToUser(long userId, SimpMessageHeaderAccessor accessor) {
        User userFrom = getCurrentUser(accessor);

        User userTo = userRepository.findById(userId).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if(userFrom.getId().equals(userTo.getId())) return;

        Optional<Follow> existedFollow = followRepository.findExistedFollow(userFrom.getId(), userTo.getId());
        if (existedFollow.isPresent()) {
            followRepository.delete(existedFollow.get());
            return;
        }

        Follow follow = Follow.builder()
                .sender(userFrom)
                .receiver(userTo)
                .status(FollowStatus.ACCEPTED)
                .build();
        followRepository.save(follow);

        Notification notification = Notification.builder()
                .content(userFrom.getFullName() + " just followed you.")
                .sendTo(userTo)
                .type(NotificationType.FOLLOW_REQUEST)
                .build();
        notificationRepository.save(notification);

        simpMessagingTemplate.convertAndSendToUser(userTo.getUsername(),
                "/queue/notifications", notificationMapper.toResponseDTO(notification));
    }

    private User getCurrentUser(SimpMessageHeaderAccessor accessor) {
        String usernameReaction = accessor.getUser().getName();

        return userRepository.findByUsername(usernameReaction)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));
    }
}
