package com.minhhai.social_network.service;

import com.minhhai.social_network.dto.response.UserDetailResponseDTO;
import com.minhhai.social_network.entity.User;
import com.minhhai.social_network.exception.AppException;
import com.minhhai.social_network.mapper.UserMapper;
import com.minhhai.social_network.repository.UserRepository;
import com.minhhai.social_network.util.commons.SecurityUtil;
import com.minhhai.social_network.util.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDetailResponseDTO getMyInformation() {
        String username = SecurityUtil.getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return convertToResponse(user);
    }

    public UserDetailResponseDTO getUserDetailInformation(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return convertToResponse(user);
    }

    private UserDetailResponseDTO convertToResponse(User user) {

        Long countPosts = userRepository.findCountPostByUsername(user.getUsername());
        Long countFollowers = userRepository.findCountFollowerByUsername(user.getUsername());
        Long countFollowing = userRepository.findCountFollowingByUsername(user.getUsername());

        UserDetailResponseDTO responseDTO = userMapper.toDetailResponseDTO(user);
        responseDTO.setCountPosts(countPosts);
        responseDTO.setCountFollowers(countFollowers);
        responseDTO.setCountFollowing(countFollowing);

        return responseDTO;
    }
}
