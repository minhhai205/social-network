package com.minhhai.social_network.mapper;

import com.minhhai.social_network.dto.response.ConversationResponseDTO;
import com.minhhai.social_network.entity.Conversation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ConversationMapper {
    ConversationResponseDTO toResponseDTO(Conversation conversation);
}
