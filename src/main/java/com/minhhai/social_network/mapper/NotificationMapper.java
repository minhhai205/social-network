package com.minhhai.social_network.mapper;

import com.minhhai.social_network.dto.response.NotificationResponseDTO;
import com.minhhai.social_network.entity.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {})
public interface NotificationMapper {
    NotificationResponseDTO toResponseDTO(Notification entity);
}
