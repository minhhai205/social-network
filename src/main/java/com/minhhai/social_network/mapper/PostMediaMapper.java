package com.minhhai.social_network.mapper;

import com.minhhai.social_network.dto.response.PostMediaResponseDTO;
import com.minhhai.social_network.entity.PostMedia;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {})
public interface PostMediaMapper {
    PostMediaResponseDTO toResponseDTO(PostMedia postMedia);
}
