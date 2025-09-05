package com.minhhai.social_network.service;

import com.minhhai.social_network.dto.request.CreateGroupRequestDTO;
import com.minhhai.social_network.dto.response.GroupMemberResponseDTO;
import com.minhhai.social_network.dto.response.GroupResponseDTO;
import com.minhhai.social_network.dto.response.JoinGroupRequestResponseDTO;
import com.minhhai.social_network.entity.Group;
import com.minhhai.social_network.entity.GroupMember;
import com.minhhai.social_network.entity.JoinGroupRequest;
import com.minhhai.social_network.entity.User;
import com.minhhai.social_network.exception.AppException;
import com.minhhai.social_network.mapper.GroupMapper;
import com.minhhai.social_network.mapper.GroupMemberMapper;
import com.minhhai.social_network.mapper.JoinGroupRequestMapper;
import com.minhhai.social_network.repository.GroupMemberRepository;
import com.minhhai.social_network.repository.GroupRepository;
import com.minhhai.social_network.repository.JoinGroupRequestRepository;
import com.minhhai.social_network.repository.UserRepository;
import com.minhhai.social_network.util.commons.SecurityUtil;
import com.minhhai.social_network.util.enums.ConversationRole;
import com.minhhai.social_network.util.enums.ErrorCode;
import com.minhhai.social_network.util.enums.GroupMemberStatus;
import com.minhhai.social_network.util.enums.GroupRole;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMapper groupMapper;
    private final JoinGroupRequestRepository joinGroupRequestRepository;
    private final JoinGroupRequestMapper joinGroupRequestMapper;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupMemberMapper groupMemberMapper;

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
                .status(GroupMemberStatus.ACTIVE)
                .build();

        Set<GroupMember> groupMembers = Set.of(groupMember);
        newGroup.setGroupMembers(groupMembers);

        groupRepository.save(newGroup);

        return groupMapper.toResponseDTO(newGroup);
    }

    public List<JoinGroupRequestResponseDTO> getAllGroupJoinRequest(long groupId) {
        String currentUsername = SecurityUtil.getCurrentUsername();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Group currentGroup = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_EXISTED));

        groupMemberRepository.findAdminOrModeratorById(currentUser.getId(), currentGroup.getId())
                .orElseThrow(() -> new AppException(ErrorCode.ACCESS_DENIED));

        List<JoinGroupRequest> requests = joinGroupRequestRepository.findAllByGroupId(groupId);

        return requests.stream().map(joinGroupRequestMapper::toResponseDTO).toList();
    }

    @Transactional
    public GroupMemberResponseDTO leaveGroup(long groupId) {
        String currentUsername = SecurityUtil.getCurrentUsername();

        Group currentGroup = groupRepository.findByIdWithUserCreated(groupId)
                .orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_EXISTED));

        // người tạo nhóm không được rời nhóm
        if(Objects.equals(currentUsername, currentGroup.getUserCreated().getUsername())){
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        GroupMember memberLeave = groupMemberRepository.findMemberByUsername(currentUsername, currentGroup.getId())
                .orElseThrow(() -> new AppException(ErrorCode.GROUP_MEMBER_NOT_EXISTED));

        memberLeave.setStatus(GroupMemberStatus.REMOVED);
        groupMemberRepository.save(memberLeave);

        return groupMemberMapper.toResponseDTO(memberLeave);
    }

    @Transactional
    public GroupMemberResponseDTO deleteMember(Long groupId, Long userId) {
        String currentUsername = SecurityUtil.getCurrentUsername();

        Group currentGroup = groupRepository.findByIdWithUserCreated(groupId)
                .orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_EXISTED));

        GroupMember currentMember = groupMemberRepository.findAdminOrModeratorByUsername(currentUsername, currentGroup.getId())
                .orElseThrow(() -> new AppException(ErrorCode.ACCESS_DENIED));

        GroupMember memberToRemove = groupMemberRepository.findMemberByMemberId(userId, currentGroup.getId())
                .orElseThrow(() -> new AppException(ErrorCode.GROUP_MEMBER_NOT_EXISTED));

        if (currentMember.getRole() == GroupRole.ADMIN) {
            // Admin có thể xóa bất kỳ ai trừ chính mình và creator
            if (userId.equals(currentMember.getUser().getId()) || userId.equals(currentGroup.getUserCreated().getId())) {
                throw new AppException(ErrorCode.INVALID_REQUEST);
            }
        } else if (currentMember.getRole() == GroupRole.MODERATOR) {
            // Moderator chỉ có thể xóa member bình thường
            if (memberToRemove.getRole() != GroupRole.MEMBER) {
                throw new AppException(ErrorCode.ACCESS_DENIED);
            }
        }

        memberToRemove.setStatus(GroupMemberStatus.REMOVED);
        groupMemberRepository.save(memberToRemove);

        return groupMemberMapper.toResponseDTO(memberToRemove);
    }
}
