package com.qnelldo.plantour.auth.service;

import com.qnelldo.plantour.auth.dto.CustomOAuth2User;
import com.qnelldo.plantour.user.dto.UserDTO;
import com.qnelldo.plantour.user.entity.UserEntity;
import com.qnelldo.plantour.user.service.UserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    public CustomOAuth2UserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String picture = oauth2User.getAttribute("picture");
        String providerId = oauth2User.getAttribute("sub");

        UserDTO userDTO = userService.processOAuthUser(email, name, picture, providerId);

        // UserDTO를 UserEntity로 변환
        UserEntity user = convertDTOtoEntity(userDTO);

        return new CustomOAuth2User(user, oauth2User.getAttributes());
    }

    private UserEntity convertDTOtoEntity(UserDTO userDTO) {
        UserEntity user = new UserEntity();
        user.setId(userDTO.getId());
        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());
        user.setProfilePicture(userDTO.getProfilePicture());
        user.setLanguageCode(userDTO.getLanguageCode());
        // 나머지 필드들도 필요에 따라 설정
        return user;
    }
}