package com.qnelldo.plantour.user.service;

import com.qnelldo.plantour.auth.service.NicknameService;
import com.qnelldo.plantour.user.entity.UserEntity;
import com.qnelldo.plantour.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final NicknameService nicknameService;

    @Autowired
    public UserService(UserRepository userRepository, NicknameService nicknameService) {
        this.nicknameService = nicknameService;
        this.userRepository = userRepository;
    }

    public UserEntity updateUserLanguage(Long userId, String languageCode) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        user.setLanguageCode(languageCode);
        return userRepository.save(user);
    }

    public UserEntity getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }

    @Transactional
    public UserEntity processOAuthUser(String email, String name, String picture, String googleId) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    UserEntity newUser = new UserEntity();
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setProfilePicture(picture);
                    newUser.setProvider(UserEntity.AuthProvider.google);
                    newUser.setProviderId(googleId);
                    newUser.setEmailVerified(true);
                    newUser.setNickname(nicknameService.generateUniqueNickname("ENG")); // 닉네임 부여
                    return userRepository.save(newUser);
                });
    }
}