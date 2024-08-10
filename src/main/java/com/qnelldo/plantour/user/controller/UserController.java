package com.qnelldo.plantour.user.controller;

import com.qnelldo.plantour.user.entity.UserEntity;
import com.qnelldo.plantour.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{userId}/language")
    public ResponseEntity<UserEntity> updateUserLanguage(@PathVariable Long userId, @RequestParam String languageCode) {
        logger.info("Updating language for user: {} to {}", userId, languageCode);
        UserEntity updatedUser = userService.updateUserLanguage(userId, languageCode);
        return ResponseEntity.ok(updatedUser);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUser(@PathVariable Long id) {
        logger.info("Fetching user with id: {}", id);
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{userId}/nickname")
    public ResponseEntity<UserEntity> updateNickname(@PathVariable Long userId, @RequestParam String newNickname) {
        logger.info("Updating nickname for user: {} to {}", userId, newNickname);
        UserEntity updatedUser = userService.updateUserNickname(userId, newNickname);
        return ResponseEntity.ok(updatedUser);
    }
}
