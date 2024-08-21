package com.qnelldo.plantour.user.service;

import com.qnelldo.plantour.common.context.LanguageContext;
import com.qnelldo.plantour.user.dto.UserDTO;
import com.qnelldo.plantour.user.entity.UserEntity;
import com.qnelldo.plantour.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
        // 새로운 닉네임 앞뒤에 있는 따옴표를 제거
        newNickname = newNickname.trim().replaceAll("^\"|\"$", "");

        // 중복 닉네임 확인을 먼저 수행
        if (userRepository.existsByCustomNickname(newNickname)) {
            throw new IllegalArgumentException("Nickname already exists: " + newNickname);
        }

        // 사용자 정보 가져오기
        UserEntity user = getUserEntityById(userId);

        // 닉네임 업데이트
        try {
            nicknameService.updateUserNickname(userId, newNickname);
        } catch (DataIntegrityViolationException e) {
            // 중복 닉네임으로 인한 데이터베이스 에러 가능성 처리
            throw new IllegalArgumentException("Nickname already exists: " + newNickname);
        }
        // 업데이트된 정보 반환
        return convertToDTO(user);
    }

    private UserDTO convertToDTO(UserEntity user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setProfilePicture(user.getProfilePicture());
        dto.setNickname(nicknameService.getLocalizedNickname(user.getId()));
        dto.setCustomNickname(user.getCustomNickname());
        dto.setLanguageCode(user.getLanguageCode());
        return dto;
    }

    public String getUserLanguage(Long userId) {
        return getUserEntityById(userId).getLanguageCode();
    }
}