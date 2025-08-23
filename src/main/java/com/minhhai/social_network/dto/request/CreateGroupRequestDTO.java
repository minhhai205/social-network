package com.minhhai.social_network.dto.request;

import com.minhhai.social_network.util.annotations.EnumValue;
import com.minhhai.social_network.util.enums.Privacy;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CreateGroupRequestDTO implements Serializable {
    @NotBlank(message = "Group name must not be blank!")
    private String name;

    private String description;

    @EnumValue(name = "Group privacy", enumClass = Privacy.class)
    private Privacy privacy;

}
