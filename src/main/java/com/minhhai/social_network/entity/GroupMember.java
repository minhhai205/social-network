package com.minhhai.social_network.entity;

import com.minhhai.social_network.util.enums.GroupMemberStatus;
import com.minhhai.social_network.util.enums.GroupRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "group_members")
public class GroupMember extends AbstractEntity<Long> {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Enumerated(EnumType.STRING)
    private GroupRole role;

    @Enumerated(EnumType.STRING)
    private GroupMemberStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime joinedAt;

    @PrePersist
    public void onJoin() {
        if (status == GroupMemberStatus.APPROVED) {
            joinedAt = LocalDateTime.now();
        }
    }
}
