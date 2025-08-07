package com.minhhai.social_network.entity;

import com.minhhai.social_network.util.enums.Privacy;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "group_table")
public class Group extends AbstractEntity<Long> {
    @NotBlank(message = "Group name must not be blank!")
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private Privacy privacy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id_created")
    private User userCreated;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "group", orphanRemoval = true)
    private Set<GroupMember> groupMembers;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "group", orphanRemoval = true)
    private Set<Post> posts;
}
