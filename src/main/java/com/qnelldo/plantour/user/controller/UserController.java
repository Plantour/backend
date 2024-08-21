package com.qnelldo.plantour.user.controller;

import com.qnelldo.plantour.auth.service.JwtTokenProvider;
import com.qnelldo.plantour.user.dto.UserDTO;
import com.qnelldo.plantour.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my")
    public ResponseEntity<UserDTO> getCurrentUser(@RequestHeader("Authorization") String token) {
        Long userId = jwtTokenProvider.extractUserIdFromAuthorizationHeader(token);
        UserDTO user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }
    //왜 안하는데!

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/nickname")
    public ResponseEntity<?> updateUserNickname(
            @RequestHeader("Authorization") String token,
            @RequestBody String newNickname) {
        try {
            Long userId = jwtTokenProvider.extractUserIdFromAuthorizationHeader(token);
            logger.info("Updating nickname for user: {} to {}", userId, newNickname);
            UserDTO updatedUser = userService.updateUserNickname(userId, newNickname);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid nickname: {}", newNickname);
            return ResponseEntity.badRequest().body("Invalid nickname");
        }
    }

}
