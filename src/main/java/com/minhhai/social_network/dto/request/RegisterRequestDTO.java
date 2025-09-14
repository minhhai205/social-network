package com.minhhai.social_network.dto.request;

import com.minhhai.social_network.util.annotations.EnumValue;
import com.minhhai.social_network.util.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class RegisterRequestDTO implements Serializable {
    @NotBlank(message = "Full name must not be blank!")
    private String fullName;

    private String firstName;

    private String lastName;

    @NotBlank(message = "Email must not be blank!")
    @Email(message = "Email is not valid!")
    private String email;

    @NotBlank(message = "Username must not be blank!")
    private String username;

    @NotBlank(message = "Password must not be blank!")
    private String password;

    @EnumValue(name = "Gender", enumClass = Gender.class)
    private Gender gender;
}
