package com.minhhai.social_network.entity;

import com.minhhai.social_network.util.enums.AuthProvider;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends AbstractEntity<Long> {
    @NotBlank(message = "Full name must not be blank!")
    private String fullName;

    private String firstName;

    private String lastName;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Email must not be blank!")
    @Email(message = "Email is not valid!")
    private String email;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Username must not be blank!")
    private String username;

    @Column(nullable = false)
    @NotBlank(message = "Password must not be blank!")
    private String password;

    @NotNull(message = "Auth provider must not be blank!")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider authProvider;

    @Column(unique = true)
    private String providerId;

    private String imageUrl;

    @ManyToMany
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;
}
