package com.minhhai.social_network.mapper;

import com.minhhai.social_network.dto.request.CreateGroupRequestDTO;
import com.minhhai.social_network.dto.response.GroupResponseDTO;
import com.minhhai.social_network.entity.Group;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {})
public interface GroupMapper {
    Group toEntity(CreateGroupRequestDTO createGroupRequestDTO);

    @Mapping(target = "userIdCreated", source = "group.userCreated.id")
    GroupResponseDTO toResponseDTO(Group group);
}
