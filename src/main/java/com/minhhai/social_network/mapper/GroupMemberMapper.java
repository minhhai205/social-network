package com.minhhai.social_network.mapper;

import com.minhhai.social_network.dto.response.GroupMemberResponseDTO;
import com.minhhai.social_network.entity.GroupMember;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface GroupMemberMapper {
    GroupMemberResponseDTO toResponseDTO(GroupMember groupMember);
}
