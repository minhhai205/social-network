package com.minhhai.social_network.entity;

import com.minhhai.social_network.util.annotations.EnumValue;
import com.minhhai.social_network.util.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "join_group_request")
public class JoinGroupRequest extends AbstractEntity<Long> {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "create_by")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Enumerated(EnumType.STRING)
    @EnumValue(name = "Join group status", enumClass = RequestStatus.class)
    private RequestStatus status;
}
