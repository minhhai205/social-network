package com.minhhai.social_network.mapper;

import com.minhhai.social_network.dto.response.JoinGroupRequestResponseDTO;
import com.minhhai.social_network.entity.JoinGroupRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface JoinGroupRequestMapper {
    JoinGroupRequestResponseDTO toResponseDTO(JoinGroupRequest joinGroupRequest);
}
