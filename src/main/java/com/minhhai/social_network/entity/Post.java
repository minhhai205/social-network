package com.minhhai.social_network.entity;

import com.minhhai.social_network.util.annotations.EnumValue;
import com.minhhai.social_network.util.enums.PostStatus;
import com.minhhai.social_network.util.enums.PostType;
import com.minhhai.social_network.util.enums.Privacy;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "posts")
public class Post extends AbstractEntity<Long>{
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id_created")
    private User userCreated;

    @Enumerated(EnumType.STRING)
    @EnumValue(name = "Post privacy", enumClass = Privacy.class)
    private Privacy privacy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Enumerated(EnumType.STRING)
    @EnumValue(name = "Post type", enumClass = PostType.class)
    private PostType postType;

    @Enumerated(EnumType.STRING)
    @EnumValue(name = "Post status", enumClass = PostStatus.class)
    private PostStatus status;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post", orphanRemoval = true)
    private Set<PostMedia> postMedia;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post", orphanRemoval = true)
    private Set<Reaction> reactions;
}
