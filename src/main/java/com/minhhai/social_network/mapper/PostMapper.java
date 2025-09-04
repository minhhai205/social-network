package com.minhhai.social_network.mapper;

import com.minhhai.social_network.dto.response.PostResponseDTO;
import com.minhhai.social_network.entity.Post;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {
        UserMapper.class,
        PostMediaMapper.class,
        GroupMapper.class
})
public interface PostMapper {
    PostResponseDTO toResponseDTO(Post post);
}
