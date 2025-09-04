package com.minhhai.social_network.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.minhhai.social_network.util.enums.PostStatus;
import com.minhhai.social_network.util.enums.PostType;
import com.minhhai.social_network.util.enums.Privacy;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@Builder
public class PostResponseDTO implements Serializable {
    private Long id;

    private String content;

    private UserResponseDTO userCreated;

    private Privacy privacy;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private GroupResponseDTO group;

    private PostType postType;

    private PostStatus status;

    private Set<PostMediaResponseDTO> postMedia;

//    private Set<PostLike> postLikes;
}
