package com.minhhai.social_network.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class GroupChatDeleteUserRequestDTO implements Serializable {
    @NotNull(message = "Conversation Id must not be empty!")
    private Long conversationId;

    @NotNull(message = "Member Id must not be empty!")
    private Long memberId;
}
