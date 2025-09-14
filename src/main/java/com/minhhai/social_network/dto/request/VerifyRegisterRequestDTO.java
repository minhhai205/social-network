package com.minhhai.social_network.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class VerifyRegisterRequestDTO implements Serializable {
    @NotBlank(message = "Email must be not null")
    private String email;

    @NotBlank(message = "Otp must be not null")
    private String otp;
}
