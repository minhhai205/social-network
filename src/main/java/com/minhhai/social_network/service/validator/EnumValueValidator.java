package com.minhhai.social_network.service.validator;

import com.minhhai.social_network.util.annotations.EnumValue;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.stream.Stream;

public class EnumValueValidator implements ConstraintValidator<EnumValue, Object> {
    private List<String> acceptedValues;

    @Override
    public void initialize(EnumValue annotation) {
        acceptedValues = Stream.of(annotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .filter(name -> !name.equalsIgnoreCase("UNKNOWN"))
                .toList();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        String enumName;
        if (value instanceof Enum<?> e) {
            enumName = e.name();
        } else if (value instanceof CharSequence cs) {
            enumName = cs.toString().toUpperCase(); // optional normalize
        } else {
            return false;
        }

        return acceptedValues.contains(enumName);
    }
}
