package com.qnelldo.plantour.user.service;

import com.qnelldo.plantour.auth.service.NicknameService;
import com.qnelldo.plantour.common.context.LanguageContext;
import com.qnelldo.plantour.user.entity.UserEntity;
import com.qnelldo.plantour.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final NicknameService nicknameService;
    private final LanguageContext languageContext;

    @Autowired
    public UserService(UserRepository userRepository, NicknameService nicknameService, LanguageContext languageContext) {
        this.languageContext = languageContext;
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
    public UserEntity processOAuthUser(String email, String name, String picture, String providerId) {
        String languageCode = languageContext.getCurrentLanguage();
        String nickname = nicknameService.generateUniqueNickname();

        return userRepository.findByEmail(email)
                .map(existingUser -> {
                    existingUser.setName(name);
                    existingUser.setProfilePicture(picture);
                    existingUser.setNickname(nickname);
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    UserEntity newUser = new UserEntity();
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setProfilePicture(picture);
                    newUser.setProvider(UserEntity.AuthProvider.google);
                    newUser.setProviderId(providerId);
                    newUser.setEmailVerified(true);
                    newUser.setNickname(nickname);
                    newUser.setLanguageCode(languageCode);
                    return userRepository.save(newUser);
                });
    }

    @Transactional
    public UserEntity updateUserNickname(Long userId, String newNickname) {
        if (userRepository.existsByNickname(newNickname)) {
            throw new RuntimeException("이미 사용 중인 닉네임입니다.");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        user.setNickname(newNickname);
        return userRepository.save(user);
    }
}