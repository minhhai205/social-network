package com.minhhai.social_network.service;

import com.minhhai.social_network.dto.response.ApiResponse.PageResponse;
import com.minhhai.social_network.dto.response.PostResponseDTO;
import com.minhhai.social_network.entity.Post;
import com.minhhai.social_network.entity.User;
import com.minhhai.social_network.exception.AppException;
import com.minhhai.social_network.mapper.PostMapper;
import com.minhhai.social_network.repository.PostRepository;
import com.minhhai.social_network.repository.UserRepository;
import com.minhhai.social_network.service.helper.PostQueryHelper;
import com.minhhai.social_network.util.commons.SecurityUtil;
import com.minhhai.social_network.util.enums.ErrorCode;
import com.minhhai.social_network.util.enums.PostStatus;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserPostService {
    private final UserRepository userRepository;
    private final PostQueryHelper postQueryHelper;
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public PageResponse<List<PostResponseDTO>> getAllPostActiveWithFilter(
            long userId, Pageable pageable, String[] filters) {

        List<String> filterList = (filters != null)
                ? new ArrayList<>(Arrays.asList(filters))
                : new ArrayList<>();

        filterList.add("userCreated.id:" + userId);
        filterList.add("status:" + "APPROVED");
        filterList.add("postType:" + "PERSONAL");

        return postQueryHelper.getAllPost(filterList, pageable);
    }

    @Transactional
    public PostResponseDTO deleteMyPost(long postId) {
        String currentUsername = SecurityUtil.getCurrentUsername();

        Post postToDelete = postRepository.findPostActiveByIdWithGroupAndUserCreated(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        if(!postToDelete.getUserCreated().getUsername().equals(currentUsername)) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        postToDelete.setStatus(PostStatus.REMOVED);
        postRepository.save(postToDelete);

        return postMapper.toResponseDTO(postToDelete);
    }
}
