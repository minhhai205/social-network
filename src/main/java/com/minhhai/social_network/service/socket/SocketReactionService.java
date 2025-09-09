package com.minhhai.social_network.service.socket;

import com.minhhai.social_network.entity.*;
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

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SocketReactionService {
    private final ReactionRepository reactionRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final CommentRepository commentRepository;

    public void handlePostReaction(long postId, SimpMessageHeaderAccessor accessor) {
        User userReaction = getCurrentUser(accessor);

        Post post = postRepository.findPostActiveByIdWithGroupAndUserCreated(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));
        validateGroupAccess(post, userReaction);

        Optional<Reaction> oldReaction = reactionRepository.findByUserIdAndPostId(userReaction.getId(), postId);
        if(oldReaction.isPresent()){
            reactionRepository.delete(oldReaction.get());
            return;
        }

        Reaction reaction = Reaction.builder()
                .userReaction(userReaction)
                .post(post)
                .build();
        reactionRepository.save(reaction);

        if(!userReaction.getId().equals(post.getUserCreated().getId())){
            Notification notification = Notification.builder()
                    .content(userReaction.getFullName() + " just reacted to your post.")
                    .sendTo(post.getUserCreated())
                    .type(NotificationType.POST_LIKE)
                    .post(post)
                    .build();
            notificationRepository.save(notification);

            simpMessagingTemplate.convertAndSendToUser(post.getUserCreated().getUsername(),
                    "/queue/notifications", notificationMapper.toResponseDTO(notification));
        }
    }

    public void handleCommentReaction(long commentId, SimpMessageHeaderAccessor accessor) {
        User userReaction = getCurrentUser(accessor);

        Comment comment = commentRepository.findByIdWithAllDetailAndGroup(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXISTED));

        Post post = comment.getPost();
        validateGroupAccess(post, userReaction);


        Optional<Reaction> oldReaction = reactionRepository.findByUserIdAndCommentId(userReaction.getId(), commentId);
        if(oldReaction.isPresent()){
            reactionRepository.delete(oldReaction.get());
            return;
        }

        Reaction reaction = Reaction.builder()
                .userReaction(userReaction)
                .comment(comment)
                .build();
        reactionRepository.save(reaction);

        if(!userReaction.getId().equals(comment.getUserCreated().getId())){
            Notification notification = Notification.builder()
                    .content(userReaction.getFullName() + " just reacted to your comment.")
                    .sendTo(comment.getUserCreated())
                    .type(NotificationType.POST_LIKE)
                    .post(post)
                    .build();
            notificationRepository.save(notification);

            simpMessagingTemplate.convertAndSendToUser(comment.getUserCreated().getUsername(),
                    "/queue/notifications", notificationMapper.toResponseDTO(notification));
        }
    }

    private User getCurrentUser(SimpMessageHeaderAccessor accessor) {
        String usernameReaction = accessor.getUser().getName();

        return userRepository.findByUsername(usernameReaction)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));
    }

    private void validateGroupAccess(Post post, User userReaction) {
        if(post.getPostType() == PostType.GROUP){
            groupMemberRepository.findMemberByMemberId(userReaction.getId(), post.getGroup().getId())
                    .orElseThrow(() -> new AppException(ErrorCode.ACCESS_DENIED));
        }
    }
}

