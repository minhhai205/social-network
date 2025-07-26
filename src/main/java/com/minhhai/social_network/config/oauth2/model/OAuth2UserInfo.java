package com.minhhai.social_network.config.oauth2.model;

import com.minhhai.social_network.exception.auth.OAuth2Exception;
import com.minhhai.social_network.util.enums.AuthProvider;
import com.minhhai.social_network.util.enums.ErrorCode;
import lombok.Getter;

import java.util.Map;

@Getter
public abstract class OAuth2UserInfo {
    protected AuthProvider authProvider;
    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public OAuth2UserInfo(AuthProvider authProvider, Map<String, Object> attributes) {
        this.attributes = attributes;
        this.authProvider = authProvider;
    }

    public abstract String getId();

    public abstract String getName();

    public abstract String getFirstName();

    public abstract String getLastName();

    public abstract String getEmail();

    public abstract String getImageUrl();

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if(registrationId.equalsIgnoreCase(AuthProvider.GOOGLE.toString())) {
            return new GoogleOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(AuthProvider.FACEBOOK.toString())) {
            return new FacebookOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(AuthProvider.GITHUB.toString())) {
            return new GithubOAuth2UserInfo(attributes);
        } else {
            throw new OAuth2Exception(ErrorCode.OAUTH2_NOT_SUPPORTED);
        }
    }

}