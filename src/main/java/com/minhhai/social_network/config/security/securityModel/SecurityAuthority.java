package com.minhhai.social_network.config.security.securityModel;

import com.minhhai.social_network.entity.Permission;
import com.minhhai.social_network.entity.Role;
import com.minhhai.social_network.exception.AppException;
import com.minhhai.social_network.util.enums.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Getter
@RequiredArgsConstructor
public class SecurityAuthority<T> implements GrantedAuthority {

    private final T authority;

    @Override
    public String getAuthority() {
        if (authority instanceof Role) {
            return "ROLE_" + ((Role) authority).getName();
        } else if (authority instanceof Permission) {
            return ((Permission) authority).getName();
        } else {
            throw new AppException(ErrorCode.AUTHORITY_NOT_SUPPORTED);
        }
    }
}
