package com.minhhai.social_network.config.oauth2.model;

import com.minhhai.social_network.util.enums.AuthProvider;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class GithubOAuth2UserInfo extends OAuth2UserInfo{
    public GithubOAuth2UserInfo(Map<String, Object> attributes) {
        super(AuthProvider.GITHUB, attributes);
    }

    @Override
    public String getId() {
        return ((Integer) attributes.get("id")).toString();
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getFirstName() {
        return null;
    }

    @Override
    public String getLastName() {
        return null;
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("avatar_url");
    }
}
