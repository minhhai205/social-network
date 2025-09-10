package com.minhhai.social_network.mapper;

import com.minhhai.social_network.dto.response.ReactionResponseDTO;
import com.minhhai.social_network.entity.Reaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ReactionMapper {
    ReactionResponseDTO toResponseDTO(Reaction reaction);
}
