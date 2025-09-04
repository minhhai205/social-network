package com.minhhai.social_network.service;

import com.minhhai.social_network.dto.response.PostResponseDTO;
import com.minhhai.social_network.entity.Group;
import com.minhhai.social_network.entity.Post;
import com.minhhai.social_network.entity.User;
import com.minhhai.social_network.exception.AppException;
import com.minhhai.social_network.mapper.PostMapper;
import com.minhhai.social_network.repository.GroupMemberRepository;
import com.minhhai.social_network.repository.GroupRepository;
import com.minhhai.social_network.repository.PostRepository;
import com.minhhai.social_network.util.commons.SecurityUtil;
import com.minhhai.social_network.util.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupPostService {
    private final GroupRepository groupRepository;
    private final PostRepository postRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final PostMapper postMapper;

    public List<PostResponseDTO> getAllCreatePostRequest(long groupId) {
        String currentUsername = SecurityUtil.getCurrentUsername();

        Group currentGroup = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_EXISTED));

        groupMemberRepository.findAdminOrModeratorByUsername(currentUsername, currentGroup.getId())
                .orElseThrow(() -> new AppException(ErrorCode.ACCESS_DENIED));

        List<Post> createPostRequests = postRepository.findAllCreatePostRequestByGroupId(groupId);

        return createPostRequests.stream().map(postMapper::toResponseDTO).toList();
    }
}
