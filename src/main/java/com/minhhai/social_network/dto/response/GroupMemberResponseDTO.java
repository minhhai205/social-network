package com.minhhai.social_network.dto.response;

import com.minhhai.social_network.util.annotations.EnumValue;
import com.minhhai.social_network.util.enums.GroupMemberStatus;
import com.minhhai.social_network.util.enums.GroupRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class GroupMemberResponseDTO implements Serializable {
    private Long id;

    private UserResponseDTO user;

    @EnumValue(name = "Group role", enumClass = GroupRole.class)
    private GroupRole role;

    @EnumValue(name = "Group member status", enumClass = GroupMemberStatus.class)
    private GroupMemberStatus status;

    private LocalDateTime joinedAt;
}
