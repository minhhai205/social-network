package com.minhhai.social_network.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "First name must not be blank!")
    private String firstName;

    @NotBlank(message = "Last name must not be blank!")
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

    @ManyToMany
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;
}
