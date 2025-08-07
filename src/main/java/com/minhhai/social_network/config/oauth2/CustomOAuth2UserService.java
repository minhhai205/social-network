package com.minhhai.social_network.config.oauth2;

import com.minhhai.social_network.config.oauth2.model.OAuth2UserInfo;
import com.minhhai.social_network.config.security.securityModel.SecurityUser;
import com.minhhai.social_network.entity.Role;
import com.minhhai.social_network.entity.User;
import com.minhhai.social_network.exception.auth.OAuth2Exception;
import com.minhhai.social_network.repository.RoleRepository;
import com.minhhai.social_network.repository.UserRepository;
import com.minhhai.social_network.util.enums.Privacy;
import com.minhhai.social_network.util.enums.AuthProvider;
import com.minhhai.social_network.util.enums.ErrorCode;
import com.minhhai.social_network.util.enums.Gender;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        return processOAuth2User(oAuth2UserRequest, oAuth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo
                .getOAuth2UserInfo(
                        oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes()
                );

        User existProviderUser = userRepository.findByProviderId(oAuth2UserInfo.getId()).orElse(null);
        if(existProviderUser != null) {
            if(!existProviderUser.getAuthProvider().equals(AuthProvider.valueOf(
                    oAuth2UserRequest.getClientRegistration().getRegistrationId().toUpperCase()))) {
                throw new OAuth2Exception(ErrorCode.OAUTH2_INVALID_PROVIDER);
            }
            updateExistingUser(existProviderUser, oAuth2UserInfo);

            return new SecurityUser(existProviderUser, oAuth2User.getAttributes());
        }

        // Nếu đã có user với email trong DB được đăng kí mặc đinh thì có thể cập nhập lại user
        // thành user của OAuth2, cái này tùy logic áp dụng....
        String email = oAuth2UserInfo.getEmail();
        User existEmailUser = (email != null) ? userRepository.findByEmail(email).orElse(null) : null;
        if(existEmailUser != null) {
            updateExistingUser(existEmailUser, oAuth2UserInfo);
            return new SecurityUser(existEmailUser, oAuth2User.getAttributes());
        }

        // Đăng kí user mới nếu chưa tồn tại user
        registerNewUser(oAuth2UserInfo);
        User newUser = userRepository.findByProviderId(oAuth2UserInfo.getId()).orElseThrow(
                () -> new OAuth2Exception(ErrorCode.USER_NOT_EXISTED));

        return new SecurityUser(newUser, oAuth2User.getAttributes());
    }

    private void registerNewUser(OAuth2UserInfo oAuth2UserInfo) {
        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new OAuth2Exception(ErrorCode.ROLE_NOT_EXISTED));

        User user = User.builder()
                .authProvider(oAuth2UserInfo.getAuthProvider())
                .providerId(oAuth2UserInfo.getId())
                .fullName(oAuth2UserInfo.getName())
                .firstName(oAuth2UserInfo.getFirstName())
                .lastName(oAuth2UserInfo.getLastName())
                .email(oAuth2UserInfo.getEmail())
                .avatarUrl(oAuth2UserInfo.getImageUrl())
                .privacy(Privacy.PUBLIC)
                .gender(Gender.PREFER_NOT_TO_SAY)
                .roles(Set.of(role))
                .username(oAuth2UserInfo.getId())
                .password(oAuth2UserInfo.getId())
                .build();
        userRepository.save(user);
    }

    private void updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        // do something.....
        existingUser.setProviderId(oAuth2UserInfo.getId());
        existingUser.setAuthProvider(oAuth2UserInfo.getAuthProvider());
        userRepository.save(existingUser);
    }
}
