package com.minhhai.social_network.mapper;

import com.minhhai.social_network.dto.response.UserDetailResponseDTO;
import com.minhhai.social_network.dto.response.UserResponseDTO;
import com.minhhai.social_network.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {PostMapper.class})
public interface UserMapper {
    UserResponseDTO toResponseDTO(User user);

    UserDetailResponseDTO toDetailResponseDTO(User user);

    @Named("toUserId")
    default long toUserId(User user) {
        return user != null ? user.getId() : null;
    }
}
