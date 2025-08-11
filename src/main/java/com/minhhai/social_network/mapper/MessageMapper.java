package com.minhhai.social_network.mapper;

import com.minhhai.social_network.dto.response.MessageResponseDTO;
import com.minhhai.social_network.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {MessageMediaMapper.class, UserMapper.class})
public interface MessageMapper {
    @Mapping(source = "userSent", target = "userIdSent", qualifiedByName = "toUserId")
    MessageResponseDTO toResponseDTO(Message entity);
}
