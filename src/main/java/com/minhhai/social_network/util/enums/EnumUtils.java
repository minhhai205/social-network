package com.minhhai.social_network.util.enums;

public class EnumUtils {
    public static <T extends Enum<T>> T fromString(Class<T> enumClass, String value, T defaultValue) {
        if (value == null) return defaultValue;
        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }
}
