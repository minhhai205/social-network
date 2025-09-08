package com.minhhai.social_network.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reactions")
public class Reaction extends AbstractEntity<Long>{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id_reaction")
    private User userReaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
}
