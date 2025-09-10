package com.minhhai.social_network.service;

import com.minhhai.social_network.dto.response.ApiResponse.PageResponse;
import com.minhhai.social_network.dto.response.PostResponseDTO;
import com.minhhai.social_network.entity.Group;
import com.minhhai.social_network.entity.Post;
import com.minhhai.social_network.exception.AppException;
import com.minhhai.social_network.mapper.PostMapper;
import com.minhhai.social_network.repository.GroupMemberRepository;
import com.minhhai.social_network.repository.GroupRepository;
import com.minhhai.social_network.repository.PostRepository;
import com.minhhai.social_network.repository.UserRepository;
import com.minhhai.social_network.service.helper.PostQueryHelper;
import com.minhhai.social_network.util.commons.SecurityUtil;
import com.minhhai.social_network.util.enums.ErrorCode;
import com.minhhai.social_network.util.enums.PostStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupPostService {
    private final GroupRepository groupRepository;
    private final PostRepository postRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final PostMapper postMapper;
    private final UserRepository userRepository;
    private final PostQueryHelper postQueryHelper;

    public PageResponse<List<PostResponseDTO>> getAllPostActiveWithFilter(
            long groupId, Pageable pageable, String... filters) {
        String currentUsername = SecurityUtil.getCurrentUsername();

        groupMemberRepository.findMemberByUsername(currentUsername, groupId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCESS_DENIED));

        List<String> filterList = (filters != null)
                ? new ArrayList<>(Arrays.asList(filters))
                : new ArrayList<>();

        filterList.add("group.id:" + groupId);
        filterList.add("status:" + "APPROVED");

        return postQueryHelper.getAllPost(filterList, pageable);
    }

    public List<PostResponseDTO> getAllCreatePostRequest(long groupId) {
        String currentUsername = SecurityUtil.getCurrentUsername();

        Group currentGroup = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_EXISTED));

        groupMemberRepository.findAdminOrModeratorByUsername(currentUsername, currentGroup.getId())
                .orElseThrow(() -> new AppException(ErrorCode.ACCESS_DENIED));

        List<Post> createPostRequests = postRepository.findAllCreatePostRequestByGroupId(groupId);

        return createPostRequests.stream().map(postMapper::toResponseDTO).toList();
    }

    @Transactional
    public PostResponseDTO deleteMyPost(long postId) {
        String currentUsername = SecurityUtil.getCurrentUsername();

        Post postToDelete = postRepository.findByIdWithGroupAndUserCreated(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        if(!postToDelete.getUserCreated().getUsername().equals(currentUsername)) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        postToDelete.setStatus(PostStatus.REMOVED);
        postRepository.save(postToDelete);

        return postMapper.toResponseDTO(postToDelete);
    }

    @Transactional
    public PostResponseDTO adminDeletePost(long postId) {
        String currentUsername = SecurityUtil.getCurrentUsername();

        Post postToDelete = postRepository.findByIdWithGroupAndUserCreated(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        groupMemberRepository.findAdminOrModeratorByUsername(currentUsername, postToDelete.getGroup().getId())
                .orElseThrow(() -> new AppException(ErrorCode.ACCESS_DENIED));

        postToDelete.setStatus(PostStatus.REMOVED);
        postRepository.save(postToDelete);

        return postMapper.toResponseDTO(postToDelete);
    }
}
