package com.qnelldo.plantour.auth.service;

import com.qnelldo.plantour.user.entity.Nickname;
import com.qnelldo.plantour.user.repository.NicknameRepository;
import com.qnelldo.plantour.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NicknameService {

    private final NicknameRepository nicknameRepository;
    private final UserRepository userRepository;

    @Autowired
    public NicknameService(NicknameRepository nicknameRepository, UserRepository userRepository) {
        this.nicknameRepository = nicknameRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public String generateUniqueNickname(String languageCode) {
        Nickname nickname = nicknameRepository.findRandomNickname();
        String baseName = languageCode.equalsIgnoreCase("KOR") ? nickname.getKoreanName() : nickname.getEnglishName();

        String generatedNickname;
        do {
            nickname.incrementUsageCount();
            generatedNickname = baseName + nickname.getUsageCount();
        } while (userRepository.existsByNickname(generatedNickname));

        nicknameRepository.save(nickname);
        return generatedNickname;
    }
}