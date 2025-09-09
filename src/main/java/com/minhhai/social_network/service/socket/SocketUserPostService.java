package com.minhhai.social_network.service.socket;

import com.minhhai.social_network.dto.request.CreatePostRequestDTO;
import com.minhhai.social_network.entity.Post;
import com.minhhai.social_network.entity.PostMedia;
import com.minhhai.social_network.entity.User;
import com.minhhai.social_network.exception.AppException;
import com.minhhai.social_network.repository.PostRepository;
import com.minhhai.social_network.repository.UserRepository;
import com.minhhai.social_network.service.FileService;
import com.minhhai.social_network.util.enums.ErrorCode;
import com.minhhai.social_network.util.enums.PostStatus;
import com.minhhai.social_network.util.enums.PostType;
import com.minhhai.social_network.util.enums.Privacy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;

@Service
@Slf4j
@RequiredArgsConstructor
public class SocketUserPostService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final FileService fileService;

    public void createPost(CreatePostRequestDTO createPostRequestDTO,
                           SimpMessageHeaderAccessor accessor) {
        String usernameCreatePost = accessor.getUser().getName();
        User userCreatePost = userRepository.findByUsername(usernameCreatePost)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

        Post newPost = Post.builder()
                .content(createPostRequestDTO.getContent())
                .userCreated(userCreatePost)
                .privacy(Privacy.PUBLIC)
                .postType(PostType.PERSONAL)
                .status(PostStatus.APPROVED)
                .build();

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

        // Send noti to some follower if there was an algorithm that could identify that follower
    }
}
