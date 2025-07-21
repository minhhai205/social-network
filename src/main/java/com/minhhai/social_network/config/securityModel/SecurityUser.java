package com.minhhai.social_network.config.securityModel;

import com.minhhai.social_network.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public class SecurityUser implements OAuth2User {
    private final User user;
    private final Map<String, Object> attributes;

    public SecurityUser(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public String getName() {
        return user.getFirstName() + user.getLastName();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        if(!CollectionUtils.isEmpty(this.user.getRoles())) {
            user.getRoles().forEach(role -> {
                authorities.add(new SecurityAuthority<>(role));
                if(!CollectionUtils.isEmpty(role.getPermissions())) {
                    role.getPermissions().forEach(permission -> {
                        authorities.add(new SecurityAuthority<>(permission));
                    });
                }
            });
        }

        return authorities;
    }
}
