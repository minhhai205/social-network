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

        if(checkOldReactionToDelete(userReaction.getId(), postId)) return;

        String contentNoti = userReaction.getFullName() + " just reacted to your post.";
        createReactionAndSendNotification(userReaction, post.getUserCreated(), post, contentNoti);
    }

    public void handleCommentReaction(long commentId, SimpMessageHeaderAccessor accessor) {
        User userReaction = getCurrentUser(accessor);

        Comment comment = commentRepository.findByIdWithAllDetailAndGroup(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXISTED));

        Post post = comment.getPost();
        validateGroupAccess(post, userReaction);

        if(checkOldReactionToDelete(userReaction.getId(), post.getId())) return;

        String contentNoti = userReaction.getFullName() + " just reacted to your comment.";
        createReactionAndSendNotification(userReaction, comment.getUserCreated(), post, contentNoti);
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

    private boolean checkOldReactionToDelete(Long userId, Long postId) {
        Optional<Reaction> oldReaction = reactionRepository.findReactionByUserIdAndPostId(userId, postId);

        if(oldReaction.isPresent()){
            reactionRepository.delete(oldReaction.get());
            return true;
        }

        return false;
    }

    private void createReactionAndSendNotification(User from, User to, Post post, String content) {
        Reaction reaction = Reaction.builder()
                .userReaction(from)
                .post(post)
                .build();
        reactionRepository.save(reaction);

        if(!from.getId().equals(to.getId())){
            Notification notification = Notification.builder()
                    .content(content)
                    .sendTo(post.getUserCreated())
                    .type(NotificationType.POST_LIKE)
                    .post(post)
                    .build();
            notificationRepository.save(notification);

            simpMessagingTemplate.convertAndSendToUser(to.getUsername(),
                    "/queue/notifications", notificationMapper.toResponseDTO(notification));
        }
    }
}

