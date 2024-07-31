package com.qnelldo.plantour.auth.service;

import com.qnelldo.plantour.auth.entity.RefreshToken;
import com.qnelldo.plantour.auth.exception.TokenRefreshException;
import com.qnelldo.plantour.auth.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private final Key key;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;
    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public JwtTokenProvider(
            @Value("${spring.security.jwt.secret-key}") String secretKey,
            @Value("${spring.security.jwt.access-token.expiration}") long accessTokenValidityInMilliseconds,
            @Value("${spring.security.jwt.refresh-token.expiration}") long refreshTokenValidityInMilliseconds,
            RefreshTokenRepository refreshTokenRepository) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String createAccessToken(Long userId) {
        return createToken(userId, accessTokenValidityInMilliseconds);
    }

    public String createRefreshToken(Long userId) {
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUserId(userId);
        if (existingToken.isPresent()) {
            RefreshToken token = existingToken.get();
            if (token.getExpiryDate().isAfter(Instant.now())) {
                // 기존 토큰이 아직 유효하면 그대로 반환
                return token.getToken();
            } else {
                // 만료된 경우 갱신
                token.setExpiryDate(Instant.now().plusMillis(refreshTokenValidityInMilliseconds));
                token.setToken(UUID.randomUUID().toString());
                refreshTokenRepository.save(token);
                return token.getToken();
            }
        } else {
            // 새 토큰 생성
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setUserId(userId);
            refreshToken.setToken(UUID.randomUUID().toString());
            refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenValidityInMilliseconds));
            refreshTokenRepository.save(refreshToken);
            return refreshToken.getToken();
        }
    }

    private String createToken(Long userId, long validityInMilliseconds) {
        Claims claims = Jwts.claims().setSubject(userId.toString());
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        return Long.parseLong(Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "리프레시 토큰이 만료되었습니다. 새로운 로그인이 필요합니다.");
        }
        return token;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public int deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByUserId(userId);
    }
}