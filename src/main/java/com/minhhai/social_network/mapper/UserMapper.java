package com.minhhai.social_network.mapper;

import com.minhhai.social_network.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {})
public interface UserMapper {
    @Named("toUserId")
    default long toUserId(User user) {
        return user != null ? user.getId() : null;
    }
}
