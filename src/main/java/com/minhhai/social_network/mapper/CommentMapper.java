package com.minhhai.social_network.mapper;

import com.minhhai.social_network.dto.response.CommentResponseDTO;
import com.minhhai.social_network.entity.Comment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CommentMapper {
    CommentResponseDTO toResponseDTO(Comment comment);
}
