package com.minhhai.social_network.entity;

import com.minhhai.social_network.util.enums.MediaType;
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

    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
}
