package com.minhhai.social_network.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "post_media")
public class PostMedia extends AbstractEntity<Long> {
    @NotBlank(message = "Media url must not be blank!")
    private String mediaUrl;

    @NotBlank(message = "Media type must not be blank!")
    private String mediaType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
}
