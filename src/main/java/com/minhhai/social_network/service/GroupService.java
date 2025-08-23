package com.minhhai.social_network.service;

import com.minhhai.social_network.dto.request.CreateGroupRequestDTO;
import com.minhhai.social_network.dto.response.GroupResponseDTO;
import com.minhhai.social_network.entity.Group;
import com.minhhai.social_network.entity.GroupMember;
import com.minhhai.social_network.entity.User;
import com.minhhai.social_network.exception.AppException;
import com.minhhai.social_network.mapper.GroupMapper;
import com.minhhai.social_network.repository.GroupRepository;
import com.minhhai.social_network.repository.UserRepository;
import com.minhhai.social_network.util.commons.SecurityUtil;
import com.minhhai.social_network.util.enums.ErrorCode;
import com.minhhai.social_network.util.enums.GroupMemberStatus;
import com.minhhai.social_network.util.enums.GroupRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMapper groupMapper;

    public GroupResponseDTO createNewGroup(CreateGroupRequestDTO createGroupRequestDTO) {
        String currentUsername = SecurityUtil.getCurrentUsername();
        User userCreated = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Group newGroup = groupMapper.toEntity(createGroupRequestDTO);
        newGroup.setUserCreated(userCreated);

        GroupMember groupMember = GroupMember.builder()
                .user(userCreated)
                .group(newGroup)
                .role(GroupRole.ADMIN)
                .status(GroupMemberStatus.APPROVED)
                .build();

        Set<GroupMember> groupMembers = Set.of(groupMember);
        newGroup.setGroupMembers(groupMembers);

        groupRepository.save(newGroup);

        return groupMapper.toResponseDTO(newGroup);
    }
}
