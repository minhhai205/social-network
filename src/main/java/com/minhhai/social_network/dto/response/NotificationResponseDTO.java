package com.minhhai.social_network.dto.response;

import com.minhhai.social_network.util.enums.NotificationType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class NotificationResponseDTO implements Serializable {
    private String content;
    private NotificationType type;
}
