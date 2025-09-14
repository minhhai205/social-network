package com.minhhai.social_network.dto.response;

import com.minhhai.social_network.entity.Post;
import com.minhhai.social_network.entity.Role;
import com.minhhai.social_network.util.enums.AuthProvider;
import com.minhhai.social_network.util.enums.Gender;
import com.minhhai.social_network.util.enums.Privacy;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@Builder
public class UserDetailResponseDTO implements Serializable {
    private Long id;

    private String fullName;

    private String firstName;

    private String lastName;

    private String email;

    private String username;

    private String avatarUrl;

    private Privacy privacy;

    private Gender gender;

    private AuthProvider authProvider;

    private Long countPosts;

    private Long countFollowers;

    private Long countFollowing;

    private boolean deleted;
}
