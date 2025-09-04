package com.minhhai.social_network.service.socket;

import com.minhhai.social_network.dto.request.CreatePostRequestDTO;
import com.minhhai.social_network.entity.*;
import com.minhhai.social_network.exception.AppException;
import com.minhhai.social_network.mapper.NotificationMapper;
import com.minhhai.social_network.repository.*;
import com.minhhai.social_network.service.FileService;
import com.minhhai.social_network.util.enums.*;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SocketGroupPostService {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final FileService fileService;
    private final PostRepository postRepository;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final NotificationMapper notificationMapper;

    public void createPostRequest(long groupId, CreatePostRequestDTO createPostRequestDTO,
                                  SimpMessageHeaderAccessor accessor) {
        String usernameCreatePost = accessor.getUser().getName();
        User userCreatePost = userRepository.findByUsername(usernameCreatePost)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

        Group currentGroup = groupRepository.findById(groupId).orElseThrow(
                () -> new AppException(ErrorCode.GROUP_NOT_EXISTED));

        GroupMember groupMemberCreatePost = groupMemberRepository.findMemberByMemberId(userCreatePost.getId(),
                        currentGroup.getId()).orElseThrow(() -> new AppException(ErrorCode.GROUP_MEMBER_NOT_EXISTED));

        Post newPost = Post.builder()
                .content(createPostRequestDTO.getContent())
                .userCreated(userCreatePost)
                .privacy(Privacy.PUBLIC)
                .group(currentGroup)
                .postType(PostType.GROUP)
                .status(PostStatus.PENDING)
                .build();

        GroupRole role = groupMemberCreatePost.getRole();
        if(role == GroupRole.ADMIN || role == GroupRole.MODERATOR) {
            newPost.setStatus(PostStatus.APPROVED);
        }

        if(createPostRequestDTO.getPostMedia() != null) {
            newPost.setPostMedia(new HashSet<>());
            createPostRequestDTO.getPostMedia().forEach(media -> {
                try{
                    String mediaUrl = fileService.upload(media, "social_network");

                    PostMedia postMedia = PostMedia.builder()
                            .mediaUrl(mediaUrl)
                            .mediaType(media.getType().split("/")[0])
                            .post(newPost)
                            .build();

                    newPost.getPostMedia().add(postMedia);
                } catch (IOException e){
                    log.error("Upload file failed: {}", media.getName(), e);
                }
            });
        }

        postRepository.save(newPost);

        // Send notification to all admin and moderator
        if(newPost.getStatus() == PostStatus.PENDING) {
            List<GroupMember> adminsAndModerators = groupMemberRepository.findAllAdminAndModeratorByGroupId(groupId);

            adminsAndModerators.forEach(admin -> {
                Notification notification = Notification.builder()
                        .content(userCreatePost.getFullName() + " created post request in group " + currentGroup.getName())
                        .type(NotificationType.GROUP_POST)
                        .group(currentGroup)
                        .sendTo(admin.getUser())
                        .build();

                notificationRepository.save(notification);

                simpMessagingTemplate.convertAndSendToUser(admin.getUser().getUsername(),
                        "/queue/notifications", notificationMapper.toResponseDTO(notification));
            });
        }

    }

    @Transactional
    public void acceptCreatePostRequest(long postId, SimpMessageHeaderAccessor accessor) {
        Post post = verifyAndGetPost(postId, accessor);

        post.setStatus(PostStatus.APPROVED);
        postRepository.save(post);

        Notification notification = Notification.builder()
                .content("Your post has been approved in the group " + post.getGroup().getName())
                .sendTo(post.getUserCreated())
                .type(NotificationType.GROUP_ACCEPT)
                .group(post.getGroup())
                .build();
        notificationRepository.save(notification);

        simpMessagingTemplate.convertAndSendToUser(post.getUserCreated().getUsername(),
                "/queue/notifications", notificationMapper.toResponseDTO(notification));
    }

    @Transactional
    public void rejectCreatePostRequest(long postId, SimpMessageHeaderAccessor accessor) {
        Post post = verifyAndGetPost(postId, accessor);

        post.setStatus(PostStatus.REJECTED);
        postRepository.save(post);

        Notification notification = Notification.builder()
                .content("Your post has been rejected in the group " + post.getGroup().getName())
                .sendTo(post.getUserCreated())
                .type(NotificationType.GROUP_POST)
                .group(post.getGroup())
                .build();
        notificationRepository.save(notification);

        simpMessagingTemplate.convertAndSendToUser(post.getUserCreated().getUsername(),
                "/queue/notifications", notificationMapper.toResponseDTO(notification));
    }

    private Post verifyAndGetPost(Long postId, SimpMessageHeaderAccessor accessor) {
        User currentUser = userRepository.findByUsername(accessor.getUser().getName())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

        Post post = postRepository.findByIdWithGroupAndUserCreated(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        groupMemberRepository.findAdminOrModeratorById(currentUser.getId(), post.getGroup().getId())
                .orElseThrow(() -> new AppException(ErrorCode.ACCESS_DENIED));

        return post;
    }
}
