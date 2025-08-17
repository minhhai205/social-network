package com.minhhai.social_network.mapper;

import com.minhhai.social_network.dto.response.NotificationResponseDTO;
import com.minhhai.social_network.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {})
public interface NotificationMapper {
    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "commentId", source = "comment.id")
    @Mapping(target = "conversationId", source = "conversation.id")
    @Mapping(target = "groupId", source = "group.id")
    NotificationResponseDTO toResponseDTO(Notification entity);
}
