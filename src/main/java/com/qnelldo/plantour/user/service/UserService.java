package com.qnelldo.plantour.user.service;

import com.qnelldo.plantour.common.context.LanguageContext;
import com.qnelldo.plantour.user.entity.Nickname;
import com.qnelldo.plantour.user.entity.UserEntity;
import com.qnelldo.plantour.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final NicknameService nicknameService;
    private final LanguageContext languageContext;

    @Autowired
    public UserService(UserRepository userRepository, NicknameService nicknameService, LanguageContext languageContext) {
        this.userRepository = userRepository;
        this.nicknameService = nicknameService;
        this.languageContext = languageContext;
    }

    public void setLanguageFromUser(Long userId) {
        UserEntity user = getUserById(userId);
        languageContext.setCurrentLanguage(user.getLanguageCode());
    }

    @Transactional
    public UserEntity updateUserLanguage(Long userId, String languageCode) {
        UserEntity user = getUserById(userId);
        user.setLanguageCode(languageCode);
        languageContext.setCurrentLanguage(languageCode);
        return userRepository.save(user);
    }

    public UserEntity getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }

    @Transactional
    public UserEntity processOAuthUser(String email, String name, String picture, String providerId) {
        return userRepository.findByEmail(email)
                .map(existingUser -> updateExistingUser(existingUser, name, picture))
                .orElseGet(() -> createNewUser(email, name, picture, providerId));
    }
    private UserEntity updateExistingUser(UserEntity user, String name, String picture) {
        user.setName(name);
        user.setProfilePicture(picture);
        // 닉네임이 없는 경우에만 새로 생성
        if (user.getNicknameId() == null) {
            Nickname nickname = nicknameService.generateUniqueNicknameEntity();
            user.setNicknameId(nickname.getId());
        }
        return userRepository.save(user);
    }

    private UserEntity createNewUser(String email, String name, String picture, String providerId) {
        Nickname nickname = nicknameService.generateUniqueNicknameEntity();
        UserEntity newUser = new UserEntity();
        newUser.setEmail(email);
        newUser.setName(name);
        newUser.setProfilePicture(picture);
        newUser.setProvider(UserEntity.AuthProvider.google);
        newUser.setProviderId(providerId);
        newUser.setEmailVerified(true);
        newUser.setNicknameId(nickname.getId());
        newUser.setLanguageCode(languageContext.getCurrentLanguage());
        return userRepository.save(newUser);
    }

    @Transactional
    public UserEntity updateUserNickname(Long userId, String newNickname) {
        UserEntity user = getUserById(userId);
        // 기존 닉네임 ID를 유지하면서 새 닉네임으로 업데이트
        nicknameService.updateUserNickname(user.getNicknameId(), newNickname);
        return user;
    }
}