package com.minhhai.social_network.entity;

import com.minhhai.social_network.util.annotations.EnumValue;
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
@Table(name = "message_media")
public class MessageMedia extends AbstractEntity<Long>{
    @NotBlank(message = "Media url must not be blank!")
    private String mediaUrl;

    @Enumerated(EnumType.STRING)
    @EnumValue(name = "Media type", enumClass = MediaType.class)
    private MediaType mediaType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private Message message;
}
