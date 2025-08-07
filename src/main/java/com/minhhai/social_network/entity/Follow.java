package com.minhhai.social_network.entity;

import com.minhhai.social_network.util.annotations.EnumValue;
import com.minhhai.social_network.util.enums.FollowStatus;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "follows")
public class Follow extends AbstractEntity<Long>{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Enumerated(EnumType.STRING)
    @EnumValue(name = "Follow status", enumClass = FollowStatus.class)
    private FollowStatus status;
}
