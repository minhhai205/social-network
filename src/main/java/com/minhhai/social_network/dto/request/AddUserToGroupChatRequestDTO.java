package com.minhhai.social_network.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
public class AddUserToGroupChatRequestDTO implements Serializable {
    @NotBlank(message = "ConversationId must not be empty!")
    private long conversationId;

    @NotBlank(message = "MemberIds must not be empty!")
    private Set<Long> memberIds;
}
