package com.minhhai.social_network.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "conversations")
public class Conversation extends AbstractEntity<Long> {
    private String name;

    private boolean isGroup;

    private String avatarUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id_created")
    private User createdBy;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "conversation", orphanRemoval = true)
    private Set<ConversationMember> conversationMember;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "conversation", orphanRemoval = true)
    private Set<Message> messages;

    private boolean deleted;
}
