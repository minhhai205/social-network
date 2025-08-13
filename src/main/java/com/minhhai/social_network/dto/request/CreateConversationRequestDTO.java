package com.minhhai.social_network.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.io.Serializable;
import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateConversationRequestDTO implements Serializable {
    private String name;

    @NotEmpty(message = "MemberIds must not be empty!")
    private Set<Long> memberIds;

    private boolean group;
}
