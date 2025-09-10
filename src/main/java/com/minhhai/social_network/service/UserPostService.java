package com.minhhai.social_network.service;

import com.minhhai.social_network.dto.response.ApiResponse.PageResponse;
import com.minhhai.social_network.dto.response.PostResponseDTO;
import com.minhhai.social_network.entity.User;
import com.minhhai.social_network.exception.AppException;
import com.minhhai.social_network.repository.UserRepository;
import com.minhhai.social_network.service.helper.PostQueryHelper;
import com.minhhai.social_network.util.commons.SecurityUtil;
import com.minhhai.social_network.util.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserPostService {
    private final UserRepository userRepository;
    private final PostQueryHelper postQueryHelper;

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
}
