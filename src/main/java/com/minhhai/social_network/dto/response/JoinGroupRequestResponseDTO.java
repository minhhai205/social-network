package com.minhhai.social_network.dto.response;

import com.minhhai.social_network.util.annotations.EnumValue;
import com.minhhai.social_network.util.enums.RequestStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class JoinGroupRequestResponseDTO implements Serializable {
    private Long id;

    private UserResponseDTO createdBy;

    @EnumValue(name = "Join group status", enumClass = RequestStatus.class)
    private RequestStatus status;
}
