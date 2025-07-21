package com.minhhai.social_network.config.oauth2.model;

import com.minhhai.social_network.util.enums.AuthProvider;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class FacebookOAuth2UserInfo extends OAuth2UserInfo{
    public FacebookOAuth2UserInfo(Map<String, Object> attributes) {
        super(AuthProvider.FACEBOOK, attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getFirstName() {
        return (String) attributes.get("first_name");
    }

    @Override
    public String getLastName() {
        return (String) attributes.get("last_name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        if(attributes.containsKey("picture")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> pictureObj = (Map<String, Object>) attributes.get("picture");
            if(pictureObj.containsKey("data")) {
                @SuppressWarnings("unchecked")
                Map<String, Object>  dataObj = (Map<String, Object>) pictureObj.get("data");
                if(dataObj.containsKey("url")) {
                    return (String) dataObj.get("url");
                }
            }
        }
        return null;
    }
}
