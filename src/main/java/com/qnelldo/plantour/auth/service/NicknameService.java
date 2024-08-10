package com.qnelldo.plantour.auth.service;

import com.qnelldo.plantour.common.context.LanguageContext;
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
    private final LanguageContext languageContext;

    public NicknameService(NicknameRepository nicknameRepository, UserRepository userRepository, LanguageContext languageContext) {
        this.languageContext = languageContext;
        this.nicknameRepository = nicknameRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public String generateUniqueNickname() {
        String languageCode = languageContext.getCurrentLanguage();
        Nickname nickname = nicknameRepository.findRandomNickname()
                .orElseThrow(() -> new RuntimeException("닉네임을 찾을 수 없습니다."));

        // 카운트 증가
        nickname.incrementUsageCount();

        // 언어에 따라 적절한 이름을 선택
        String baseName = languageCode.equalsIgnoreCase("KOR") ? nickname.getKoreanName() : nickname.getEnglishName();

        // 최종 닉네임 생성
        String generatedNickname = baseName + nickname.getUsageCount();

        // 중복된 닉네임이 없는지 확인
        while (userRepository.existsByNickname(generatedNickname)) {
            nickname.incrementUsageCount();
            generatedNickname = baseName + nickname.getUsageCount();
        }

        nicknameRepository.save(nickname);
        return generatedNickname;
    }
}