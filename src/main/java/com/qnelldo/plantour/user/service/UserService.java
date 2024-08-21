package com.qnelldo.plantour.user.service;

import com.qnelldo.plantour.common.context.LanguageContext;
import com.qnelldo.plantour.user.dto.UserDTO;
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

    @Transactional
    public UserDTO updateUserLanguage(Long userId, String languageCode) {
        if (!isValidLanguageCode(languageCode)) {
            throw new IllegalArgumentException("Invalid language code: " + languageCode);
        }

        UserEntity user = getUserEntityById(userId);
        user.setLanguageCode(languageCode.toUpperCase());
        languageContext.setCurrentLanguage(languageCode.toUpperCase());
        return convertToDTO(userRepository.save(user));
    }

    private boolean isValidLanguageCode(String languageCode) {
        return "KOR".equalsIgnoreCase(languageCode) || "ENG".equalsIgnoreCase(languageCode);
    }

    public UserDTO getUserById(Long id) {
        return convertToDTO(getUserEntityById(id));
    }

    private UserEntity getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }

    @Transactional
    public UserDTO processOAuthUser(String email, String name, String picture, String providerId) {
        return userRepository.findByEmail(email)
                .map(existingUser -> updateExistingUser(existingUser, name, picture))
                .orElseGet(() -> createNewUser(email, name, picture, providerId));
    }

    private UserDTO updateExistingUser(UserEntity user, String name, String picture) {
        user.setName(name);
        user.setProfilePicture(picture);
        if (user.getNicknameId() == null) {
            nicknameService.assignUniqueNickname(user);
        }
        return convertToDTO(userRepository.save(user));
    }

    private UserDTO createNewUser(String email, String name, String picture, String providerId) {
        UserEntity newUser = new UserEntity();
        newUser.setEmail(email);
        newUser.setName(name);
        newUser.setProfilePicture(picture);
        newUser.setProvider(UserEntity.AuthProvider.google);
        newUser.setProviderId(providerId);
        newUser.setEmailVerified(true);
        newUser.setLanguageCode(languageContext.getCurrentLanguage());

        UserEntity savedUser = userRepository.save(newUser);
        nicknameService.assignUniqueNickname(savedUser);

        return convertToDTO(savedUser);
    }

    @Transactional
    public UserDTO updateUserNickname(Long userId, String newNickname) {
        UserEntity user = getUserEntityById(userId);
        nicknameService.updateUserNickname(userId, newNickname);
        return convertToDTO(user);
    }

    private UserDTO convertToDTO(UserEntity user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setProfilePicture(user.getProfilePicture());
        dto.setNickname(nicknameService.getLocalizedNickname(user.getId()));
        dto.setCustomNickname(user.getCustomNickname() != null);
        dto.setLanguageCode(user.getLanguageCode());
        return dto;
    }

    public String getUserLanguage(Long userId) {
        return getUserEntityById(userId).getLanguageCode();
    }
}