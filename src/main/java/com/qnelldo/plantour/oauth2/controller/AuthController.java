package com.qnelldo.plantour.oauth2.controller;

import com.qnelldo.plantour.oauth2.JwtTokenProvider;
import com.qnelldo.plantour.oauth2.OAuth2Service;
import com.qnelldo.plantour.oauth2.dto.AuthResponse;
import com.qnelldo.plantour.user.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final OAuth2Service oAuth2Service;
    private final JwtTokenProvider tokenProvider;

    @Autowired
    public AuthController(OAuth2Service oAuth2Service, JwtTokenProvider tokenProvider) {
        this.oAuth2Service = oAuth2Service;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/google")
    public ResponseEntity<?> authenticateUser(@RequestParam("code") String code) {
        System.out.println("Received code: " + code); // 디버깅을 위해 추가
        String accessToken = oAuth2Service.getAccessToken(code);
        UserEntity user = oAuth2Service.getUserInfo(accessToken);
        String jwt = tokenProvider.createToken(user.getId());
        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}