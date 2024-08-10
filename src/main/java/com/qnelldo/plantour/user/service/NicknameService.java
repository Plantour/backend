package com.qnelldo.plantour.user.service;

import com.qnelldo.plantour.common.context.LanguageContext;
import com.qnelldo.plantour.user.entity.Nickname;
import com.qnelldo.plantour.user.entity.UserEntity;
import com.qnelldo.plantour.user.repository.NicknameRepository;
import com.qnelldo.plantour.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NicknameService {
    private final NicknameRepository nicknameRepository;
    private final LanguageContext languageContext;
    private final UserRepository userRepository;

    public NicknameService(NicknameRepository nicknameRepository, LanguageContext languageContext, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.nicknameRepository = nicknameRepository;
        this.languageContext = languageContext;
    }

    @Transactional
    public Nickname generateUniqueNicknameEntity() {
        Nickname nickname = nicknameRepository.findRandomNickname()
                .orElseThrow(() -> new RuntimeException("No available nicknames"));
        nickname.incrementUsageCount();
        return nicknameRepository.save(nickname);
    }

    @Transactional
    public void updateUserNickname(Long userId, String newNickname) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setCustomNickname(newNickname);
        userRepository.save(user);
    }

    public String getLocalizedNickname(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getCustomNickname() != null) {
            return user.getCustomNickname();
        }

        Nickname nickname = nicknameRepository.findById(user.getNicknameId())
                .orElseThrow(() -> new RuntimeException("Nickname not found"));

        String languageCode = languageContext.getCurrentLanguage();
        String baseNickname = nickname.getLocalizedNickname(languageCode);
        return baseNickname + nickname.getUsageCount();
    }


    @Transactional
    public void setCustomNickname(Long userId, String customNickname) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setCustomNickname(customNickname);
        userRepository.save(user);
    }

    @Transactional
    public void removeCustomNickname(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setCustomNickname(null);
        userRepository.save(user);
    }

    public boolean hasCustomNickname(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getCustomNickname() != null;
    }
}