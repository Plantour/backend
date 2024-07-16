package com.qnelldo.plantour.user.controller;

import com.qnelldo.plantour.user.dto.UserRegistrationDto;
import com.qnelldo.plantour.user.entity.UserEntity;
import com.qnelldo.plantour.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 사용자의 언어 설정을 변경합니다.
     * @param userId 사용자 ID
     * @param languageCode 변경할 언어 코드
     * @return 업데이트된 UserEntity
     */

    @PutMapping("/{userId}/language")
    public ResponseEntity<UserEntity> updateUserLanguage(@PathVariable Long userId, @RequestParam String languageCode) {
        UserEntity updatedUser = userService.updateUserLanguage(userId, languageCode);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        try {
            UserEntity registeredUser = userService.registerUser(registrationDto);
            return ResponseEntity.ok("사용자가 성공적으로 등록되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 추가적인 CRUD 작업을 위한 엔드포인트
    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserEntity> updateUser(@PathVariable Long id, @RequestBody UserRegistrationDto userDto) {
        return ResponseEntity.ok(userService.updateUser(id, userDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("사용자가 성공적으로 삭제되었습니다.");
    }

    /*
    // OAuth2 관련 엔드포인트 (나중에 사용)
    @GetMapping("/me")
    public ResponseEntity<UserEntity> getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
        // OAuth2 사용자 정보 반환 로직
    }
    */
}