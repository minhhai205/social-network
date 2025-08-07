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
@Table(name = "messages")
public class Message extends AbstractEntity<Long>{
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id_sent")
    private User userSent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "message", orphanRemoval = true)
    private Set<MessageMedia> messageMedia;

    private boolean isDeleted;
}
