package com.qnelldo.plantour.user.controller;

import com.qnelldo.plantour.auth.service.JwtTokenProvider;
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
    private final JwtTokenProvider jwtTokenProvider;

    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @PutMapping("/{userId}/language")
    public ResponseEntity<UserEntity> updateUserLanguage(@PathVariable Long userId, @RequestParam String languageCode) {
        logger.info("Updating language for user: {} to {}", userId, languageCode);
        UserEntity updatedUser = userService.updateUserLanguage(userId, languageCode);
        return ResponseEntity.ok(updatedUser);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{userId}/nickname")
    public ResponseEntity<UserEntity> updateNickname(@RequestHeader String authorization, @RequestParam String newNickname) {
        Long userId = jwtTokenProvider.extractUserIdFromAuthorizationHeader(authorization);
        logger.info("Updating nickname for user: {} to {}", userId, newNickname);
        UserEntity updatedUser = userService.updateUserNickname(userId, newNickname);
        return ResponseEntity.ok(updatedUser);
    }
}
