package com.minhhai.social_network.service.socket;

import com.minhhai.social_network.dto.request.CommentRequestDTO;
import com.minhhai.social_network.entity.Comment;
import com.minhhai.social_network.entity.Notification;
import com.minhhai.social_network.entity.Post;
import com.minhhai.social_network.entity.User;
import com.minhhai.social_network.exception.AppException;
import com.minhhai.social_network.mapper.NotificationMapper;
import com.minhhai.social_network.repository.*;
import com.minhhai.social_network.util.enums.ErrorCode;
import com.minhhai.social_network.util.enums.NotificationType;
import com.minhhai.social_network.util.enums.PostType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class SocketCommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final NotificationMapper notificationMapper;

    public void createPostComment(long postId, CommentRequestDTO commentRequestDTO,
                                  SimpMessageHeaderAccessor accessor) {
        String currentUsername = accessor.getUser().getName();
        User userComment = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

        Post post = postRepository.findPostActiveByIdWithGroupAndUserCreated(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        if(post.getPostType() == PostType.GROUP){
            groupMemberRepository.findMemberByMemberId(userComment.getId(), post.getGroup().getId())
                    .orElseThrow(() -> new AppException(ErrorCode.ACCESS_DENIED));
        }

        Comment comment = Comment.builder()
                .content(commentRequestDTO.getContent())
                .userCreated(userComment)
                .post(post)
                .build();
        commentRepository.save(comment);

        if(!userComment.getId().equals(post.getUserCreated().getId())){
            Notification notification = Notification.builder()
                    .content(userComment.getFullName() + " just liked one of your posts.")
                    .sendTo(post.getUserCreated())
                    .type(NotificationType.COMMENT)
                    .post(post)
                    .build();
            notificationRepository.save(notification);

            simpMessagingTemplate.convertAndSendToUser(post.getUserCreated().getUsername(),
                    "/queue/notifications", notificationMapper.toResponseDTO(notification));
        }
    }
}
