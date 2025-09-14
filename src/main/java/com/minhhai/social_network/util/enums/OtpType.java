package com.minhhai.social_network.util.enums;

import lombok.Getter;

@Getter
public enum OtpType {
    OTP_REGISTER("otp_register:"),
    OTP_RESET_PASSWORD("otp_reset_password:");

    private final String value;

    OtpType(String value) {
        this.value = value;
    }

}