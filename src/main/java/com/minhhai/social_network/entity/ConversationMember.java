package com.minhhai.social_network.entity;

import com.minhhai.social_network.util.annotations.EnumValue;
import com.minhhai.social_network.util.enums.ConversationRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "conversation_member")
public class ConversationMember extends AbstractEntity<Long> {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @Enumerated(EnumType.STRING)
    @EnumValue(name = "Conversation role", enumClass = ConversationRole.class)
    private ConversationRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_message_read_id")
    private Message lastMessageRead;

    private boolean deleted;
}
