package com.qnelldo.plantour.user.controller;

import com.qnelldo.plantour.user.entity.UserEntity;
import com.qnelldo.plantour.user.service.UserService;
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

    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // register, update, delete 엔드포인트 제거
}