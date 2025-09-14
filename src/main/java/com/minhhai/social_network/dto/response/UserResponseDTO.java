package com.minhhai.social_network.dto.response;

import com.minhhai.social_network.util.enums.Gender;
import com.minhhai.social_network.util.enums.Privacy;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class UserResponseDTO implements Serializable {
    private Long id;

    private String fullName;

    private String firstName;

    private String lastName;

    private String email;

    private String username;

    private String avatarUrl;

    private Privacy privacy;

    private Gender gender;
}
