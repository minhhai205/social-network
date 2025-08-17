package com.minhhai.social_network.dto.response;

import com.minhhai.social_network.util.enums.NotificationType;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationResponseDTO implements Serializable {
    private Long id;
    private String content;
    private NotificationType type;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long postId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long commentId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long conversationId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long groupId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
