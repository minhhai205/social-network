package com.minhhai.social_network.service.helper;

import com.minhhai.social_network.dto.response.ApiResponse.PageResponse;
import com.minhhai.social_network.dto.response.PostResponseDTO;
import com.minhhai.social_network.entity.Post;
import com.minhhai.social_network.mapper.PostMapper;
import com.minhhai.social_network.repository.PostRepository;
import com.minhhai.social_network.repository.specification.SpecificationsBuilder;
import com.minhhai.social_network.util.commons.AppConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostQueryHelper {
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public PageResponse<List<PostResponseDTO>> getAllPost(List<String> filterList, Pageable pageable) {
        SpecificationsBuilder builder = new SpecificationsBuilder();
        Pattern pattern = Pattern.compile(AppConst.SEARCH_SPEC_OPERATOR);

        for (String data : filterList) {
            Matcher matcher = pattern.matcher(data);

            if (matcher.find()) {
                builder.with(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4),
                        matcher.group(5), matcher.group(6));
            }
        }

        Page<Post> posts = postRepository.findAll(builder.build(), pageable);

        return convertToPageResponse(posts, pageable);
    }

    private PageResponse<List<PostResponseDTO>> convertToPageResponse(Page<Post> posts, Pageable pageable) {
        List<Long> postIds = posts.getContent().stream().map(Post::getId).toList();

        List<Post> postList = postRepository.findByIdIn(postIds);

        Map<Long, Post> postMap = postList.stream().collect(Collectors.toMap(Post::getId, p -> p));

        List<PostResponseDTO> response = postIds.stream()
                        .map(postMap::get)
                        .map(postMapper::toResponseDTO)
                        .toList();

        getTotalReactionForComment(response);

        log.info("--------- Get posts successfully ----------");

        return PageResponse.<List<PostResponseDTO>>builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPage(posts.getTotalPages())
                .items(response)
                .build();
    }

    private void getTotalReactionForComment(List<PostResponseDTO> postResponses) {
        List<Long> postIds = postResponses.stream().map(PostResponseDTO::getId).toList();

        Map<Long, Long> reactionCountMap = postRepository.findCountReactionForPosts(postIds).stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));

        Map<Long, Long> commentCountMap = postRepository.findCountCommentForPosts(postIds).stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));

        postResponses.forEach(post -> {
            post.setCountReactions(reactionCountMap.get(post.getId()));
            post.setCountComments(commentCountMap.get(post.getId()));
        });
    }
}
