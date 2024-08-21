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
        this.nicknameRepository = nicknameRepository;
        this.languageContext = languageContext;
        this.userRepository = userRepository;
    }

    @Transactional
    public void assignUniqueNickname(UserEntity user) {
        Nickname nickname = nicknameRepository.findRandomNickname()
                .orElseThrow(() -> new RuntimeException("No available nicknames"));
        nickname.incrementUsageCount();
        nicknameRepository.save(nickname);

        user.setNicknameId(nickname.getId());
        user.setNicknameCount(nickname.getUsageCount());
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
        return baseNickname + user.getNicknameCount();
    }

    @Transactional
    public void updateUserNickname(Long userId, String newNickname) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setCustomNickname(newNickname);
        userRepository.save(user);
    }

}