package com.minhhai.social_network.service;


import com.minhhai.social_network.dto.response.ApiResponse.PageResponse;
import com.minhhai.social_network.dto.response.CommentResponseDTO;
import com.minhhai.social_network.dto.response.ConversationResponseDTO;
import com.minhhai.social_network.entity.Comment;
import com.minhhai.social_network.entity.Conversation;
import com.minhhai.social_network.entity.User;
import com.minhhai.social_network.mapper.ConversationMapper;
import com.minhhai.social_network.repository.ConversationRepository;
import com.minhhai.social_network.repository.UserRepository;
import com.minhhai.social_network.repository.specification.SpecificationsBuilder;
import com.minhhai.social_network.util.commons.AppConst;
import com.minhhai.social_network.util.commons.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConversationService {
    private final ConversationRepository conversationRepository;
    private final ConversationMapper conversationMapper;

    public PageResponse<List<ConversationResponseDTO>> getAllMyConversationWithFilter(
            Pageable pageable, String[] filters) {
        String currentUsername = SecurityUtil.getCurrentUsername();

        List<String> filterList = (filters != null)
                ? new ArrayList<>(Arrays.asList(filters))
                : new ArrayList<>();

        filterList.add("deleted:" + false);
        filterList.add("conversationMember.deleted:" + false);
        filterList.add("conversationMember.user.username:" + currentUsername);

        return getAllConversation(filterList, pageable);
    }

    private PageResponse<List<ConversationResponseDTO>> getAllConversation(List<String> filterList, Pageable pageable) {
        SpecificationsBuilder builder = new SpecificationsBuilder();
        Pattern pattern = Pattern.compile(AppConst.SEARCH_SPEC_OPERATOR);

        for (String data : filterList) {
            Matcher matcher = pattern.matcher(data);

            if (matcher.find()) {
                builder.with(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4),
                        matcher.group(5), matcher.group(6));
            }
        }

        Page<Conversation> conversations = conversationRepository.findAll(builder.build(), pageable);

        return convertToPageResponse(conversations, pageable);
    }

    private PageResponse<List<ConversationResponseDTO>> convertToPageResponse(
            Page<Conversation> conversations, Pageable pageable) {

        List<ConversationResponseDTO> response = conversations.getContent().stream()
                .map(conversationMapper::toResponseDTO).toList();

        log.info("--------- Get conversation successfully ----------");

        return PageResponse.<List<ConversationResponseDTO>>builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPage(conversations.getTotalPages())
                .items(response)
                .build();
    }
}
