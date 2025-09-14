package com.minhhai.social_network.service;

import com.minhhai.social_network.dto.response.ApiResponse.PageResponse;
import com.minhhai.social_network.dto.response.ReactionResponseDTO;
import com.minhhai.social_network.entity.Reaction;
import com.minhhai.social_network.mapper.ReactionMapper;
import com.minhhai.social_network.repository.ReactionRepository;
import com.minhhai.social_network.repository.specification.SpecificationsBuilder;
import com.minhhai.social_network.util.commons.AppConst;
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
public class ReactionService {
    private final ReactionRepository reactionRepository;
    private final ReactionMapper reactionMapper;

    public PageResponse<List<ReactionResponseDTO>> getAllPostReactionWithFilter(
            long postId, Pageable pageable, String[] filters) {

        List<String> filterList = (filters != null)
                ? new ArrayList<>(Arrays.asList(filters))
                : new ArrayList<>();

        filterList.add("post.id:" + postId);

        return getAllReaction(filterList, pageable);
    }

    public PageResponse<List<ReactionResponseDTO>> getAllCommentReactionWithFilter(
            long commentId, Pageable pageable, String[] filters) {

        List<String> filterList = (filters != null)
                ? new ArrayList<>(Arrays.asList(filters))
                : new ArrayList<>();

        filterList.add("comment.id:" + commentId);

        return getAllReaction(filterList, pageable);
    }

    private PageResponse<List<ReactionResponseDTO>> getAllReaction(List<String> filterList, Pageable pageable) {
        SpecificationsBuilder builder = new SpecificationsBuilder();
        Pattern pattern = Pattern.compile(AppConst.SEARCH_SPEC_OPERATOR);

        for (String data : filterList) {
            Matcher matcher = pattern.matcher(data);

            if (matcher.find()) {
                builder.with(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4),
                        matcher.group(5), matcher.group(6));
            }
        }

        Page<Reaction> reactions = reactionRepository.findAll(builder.build(), pageable);

        return convertToPageResponse(reactions, pageable);
    }

    private PageResponse<List<ReactionResponseDTO>> convertToPageResponse(Page<Reaction> reactions, Pageable pageable) {

        List<ReactionResponseDTO> response = reactions.getContent().stream()
                .map(reactionMapper::toResponseDTO).toList();

        log.info("--------- Get reactions successfully ----------");

        return PageResponse.<List<ReactionResponseDTO>>builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPage(reactions.getTotalPages())
                .items(response)
                .build();
    }
}
