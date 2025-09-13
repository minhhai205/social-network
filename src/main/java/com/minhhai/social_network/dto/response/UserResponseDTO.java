package com.minhhai.social_network.dto.response;

import com.minhhai.social_network.entity.Follow;
import com.minhhai.social_network.entity.Post;
import com.minhhai.social_network.entity.Role;
import com.minhhai.social_network.util.annotations.EnumValue;
import com.minhhai.social_network.util.enums.AuthProvider;
import com.minhhai.social_network.util.enums.Gender;
import com.minhhai.social_network.util.enums.Privacy;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

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
