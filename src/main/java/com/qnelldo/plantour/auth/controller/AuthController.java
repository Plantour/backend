package com.qnelldo.plantour.auth.controller;

import com.qnelldo.plantour.auth.dto.AuthResponse;
import com.qnelldo.plantour.auth.dto.RefreshTokenRequest;
import com.qnelldo.plantour.auth.entity.RefreshToken;
import com.qnelldo.plantour.auth.service.JwtTokenProvider;
import com.qnelldo.plantour.auth.service.OAuth2Service;
import com.qnelldo.plantour.common.context.LanguageContext;
import com.qnelldo.plantour.user.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final OAuth2Service oAuth2Service;
    private final JwtTokenProvider tokenProvider;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final LanguageContext languageContext;

    public AuthController(OAuth2Service oAuth2Service, JwtTokenProvider tokenProvider, LanguageContext languageContext) {
        this.oAuth2Service = oAuth2Service;
        this.tokenProvider = tokenProvider;
        this.languageContext = languageContext;
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
            return processGoogleAuthentication(code);
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
                    String languageCode = languageContext.getCurrentLanguage();
                    logger.info("Creating new access token: {}", newAccessToken);
                    // 리프레시 토큰은 재사용
                    return ResponseEntity.ok(new AuthResponse(newAccessToken, requestRefreshToken, languageCode));
                })
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다."));
    }

    @GetMapping("/check-token")
    public ResponseEntity<Map<String, Boolean>> checkToken(@RequestHeader("Authorization") String token) {
        // "Bearer " 접두사가 있을 경우 제거

        logger.info("Checking token: {}", token);

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        boolean isValid = tokenProvider.validateToken(token);
        logger.info("Token is valid: {}", isValid);
        return ResponseEntity.ok(Map.of("valid", isValid));
    }

    private ResponseEntity<?> processGoogleAuthentication(String code) throws Exception {
        try {
            String accessToken = oAuth2Service.getAccessToken(code);
            logger.info("Obtained access token from Google");
            UserDTO user = oAuth2Service.getUserInfo(accessToken);
            logger.info("Retrieved user info: {}", user);

            // 언어 설정은 인터셉터에서 처리되므로 여기서는 제거합니다.
            // userService.setLanguageFromUser(user.getId());

            String jwt = tokenProvider.createAccessToken(user.getId());
            String refreshToken = tokenProvider.createRefreshToken(user.getId());
            String languageCode = languageContext.getCurrentLanguage();

            logger.info("Created JWT token: {}", jwt);
            logger.info("Created refresh token: {}", refreshToken);
            logger.info("Language code: {}", languageCode);

            return ResponseEntity.ok(new AuthResponse(jwt, refreshToken, languageCode));
        } catch (Exception e) {
            logger.error("인증 처리 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("인증 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

}