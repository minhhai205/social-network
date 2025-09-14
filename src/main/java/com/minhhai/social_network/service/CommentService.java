package com.minhhai.social_network.service;

import com.minhhai.social_network.dto.response.ApiResponse.PageResponse;
import com.minhhai.social_network.dto.response.CommentResponseDTO;
import com.minhhai.social_network.entity.Comment;
import com.minhhai.social_network.mapper.CommentMapper;
import com.minhhai.social_network.repository.CommentRepository;
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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public PageResponse<List<CommentResponseDTO>> getAllCommentWithFilter(
            long postId, Pageable pageable, String... filters) {

        List<String> filterList = (filters != null)
                ? new ArrayList<>(Arrays.asList(filters))
                : new ArrayList<>();

        filterList.add("post.id:" + postId);

        return getAllComment(filterList, pageable);
    }

    public PageResponse<List<CommentResponseDTO>> getAllReplyCommentWithFilter(long commentId, Pageable pageable, String[] filters) {
        List<String> filterList = (filters != null)
                ? new ArrayList<>(Arrays.asList(filters))
                : new ArrayList<>();

        filterList.add("parentComment.id:" + commentId);

        return getAllComment(filterList, pageable);
    }

    private PageResponse<List<CommentResponseDTO>> getAllComment(List<String> filterList, Pageable pageable) {
        SpecificationsBuilder builder = new SpecificationsBuilder();
        Pattern pattern = Pattern.compile(AppConst.SEARCH_SPEC_OPERATOR);

        for (String data : filterList) {
            Matcher matcher = pattern.matcher(data);

            if (matcher.find()) {
                builder.with(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4),
                        matcher.group(5), matcher.group(6));
            }
        }

        Page<Comment> comments = commentRepository.findAll(builder.build(), pageable);

        return convertToPageResponse(comments, pageable);
    }

    private PageResponse<List<CommentResponseDTO>> convertToPageResponse(Page<Comment> comments, Pageable pageable) {

        List<CommentResponseDTO> response = comments.getContent().stream()
                .map(commentMapper::toResponseDTO).toList();

        getTotalReactionForComment(response);

        log.info("--------- Get comments successfully ----------");

        return PageResponse.<List<CommentResponseDTO>>builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPage(comments.getTotalPages())
                .items(response)
                .build();
    }

    private void getTotalReactionForComment(List<CommentResponseDTO> commentResponses) {
        List<Long> commentIds = commentResponses.stream().map(CommentResponseDTO::getId).toList();

        Map<Long, Long> reactionCountMap = commentRepository.findCountReactionForComments(commentIds).stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));

        commentResponses.forEach(comment -> {
            comment.setCountReactions(reactionCountMap.get(comment.getId()));
        });
    }


}
