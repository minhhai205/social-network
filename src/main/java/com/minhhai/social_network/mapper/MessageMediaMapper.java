package com.minhhai.social_network.mapper;

import com.minhhai.social_network.dto.response.MessageMediaResponseDTO;
import com.minhhai.social_network.entity.MessageMedia;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {})
public interface MessageMediaMapper {
    MessageMediaResponseDTO toResponseDTO(MessageMedia entity);
}
