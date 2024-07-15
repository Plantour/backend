package com.qnelldo.plantour.user.service;

import com.qnelldo.plantour.user.dto.UserRegistrationDto;
import com.qnelldo.plantour.user.entity.UserEntity;
import com.qnelldo.plantour.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    // private final PasswordEncoder passwordEncoder;

    public UserEntity registerUser(UserRegistrationDto registrationDto) {
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }

        UserEntity user = new UserEntity();
        user.setEmail(registrationDto.getEmail());
        user.setPassword(registrationDto.getPassword()); // 실제 운영 시에는 인코딩 필요
        // user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setName(registrationDto.getName());
        user.setProvider(UserEntity.AuthProvider.local);
        user.setEmailVerified(false);

        return userRepository.save(user);
    }

    public UserEntity getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }

    public UserEntity updateUser(Long id, UserRegistrationDto userDto) {
        UserEntity user = getUserById(id);
        user.setName(userDto.getName());
        // 비밀번호 변경 로직 (필요시 추가)
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /*
    // OAuth2 관련 메서드 (나중에 사용)
    public UserEntity processOAuth2User(OAuth2UserInfo oAuth2UserInfo) {
        // OAuth2 사용자 처리 로직
    }
    */
}