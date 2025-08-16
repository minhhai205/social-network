package com.minhhai.social_network.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
public class GroupChatAddUserRequestDTO implements Serializable {
    @NotNull(message = "ConversationId must not be empty!")
    private Long conversationId;

    @NotEmpty(message = "MemberIds must not be empty!")
    private Set<Long> memberIds;
}
