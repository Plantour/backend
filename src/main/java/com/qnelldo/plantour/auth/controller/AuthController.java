package com.qnelldo.plantour.auth.controller;

import com.qnelldo.plantour.auth.service.JwtTokenProvider;
import com.qnelldo.plantour.auth.service.OAuth2Service;
import com.qnelldo.plantour.auth.dto.AuthResponse;
import com.qnelldo.plantour.auth.dto.RefreshTokenRequest;
import com.qnelldo.plantour.user.entity.UserEntity;
import com.qnelldo.plantour.auth.entity.RefreshToken;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final OAuth2Service oAuth2Service;
    private final JwtTokenProvider tokenProvider;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    public AuthController(OAuth2Service oAuth2Service, JwtTokenProvider tokenProvider) {
        this.oAuth2Service = oAuth2Service;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/google")
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> payload) {
        logger.info("Received request body: {}", payload);
        String code = payload.get("code");

        if (code == null || code.isEmpty()) {
            logger.warn("인증 코드 누락");
            return ResponseEntity.badRequest().body("인증 코드가 누락되었습니다.");
        }

        try {
            String accessToken = oAuth2Service.getAccessToken(code);
            UserEntity user = oAuth2Service.getUserInfo(accessToken);
            String jwt = tokenProvider.createAccessToken(user.getId());
            String refreshToken = tokenProvider.createRefreshToken(user.getId());
            return ResponseEntity.ok(new AuthResponse(jwt, refreshToken));
        } catch (Exception e) {
            logger.error("인증 처리 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("인증 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }


    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        String requestRefreshToken = refreshTokenRequest.getRefreshToken();

        return tokenProvider.findByToken(requestRefreshToken)
                .map(tokenProvider::verifyExpiration)
                .map(RefreshToken::getUserId)
                .map(userId -> {
                    String newAccessToken = tokenProvider.createAccessToken(userId);
                    // 리프레시 토큰은 재사용
                    return ResponseEntity.ok(new AuthResponse(newAccessToken, requestRefreshToken));
                })
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다."));
    }
}