package com.qnelldo.plantour.common.interceptor;

import com.qnelldo.plantour.auth.service.JwtTokenProvider;
import com.qnelldo.plantour.common.context.LanguageContext;
import com.qnelldo.plantour.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;

@Component
public class LanguageInterceptor implements HandlerInterceptor {
    private final LanguageContext languageContext;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private Logger logger = LoggerFactory.getLogger(LanguageInterceptor.class);

    public LanguageInterceptor(LanguageContext languageContext, JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.languageContext = languageContext;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String languageHeader = request.getHeader("X-User-Language");
        String token = request.getHeader("Authorization");

        if (languageHeader != null && (languageHeader.equalsIgnoreCase("KOR") || languageHeader.equalsIgnoreCase("ENG"))) {
            languageContext.setCurrentLanguage(languageHeader.toUpperCase());
        } else {
            languageContext.setCurrentLanguage("ENG");
        }

        if (token != null && token.startsWith("Bearer ")) {
            try {
                Long userId = jwtTokenProvider.extractUserIdFromAuthorizationHeader(token);
                if (userId != null) {
                    String userLanguage = userService.getUserLanguage(userId);
                    if (!userLanguage.equalsIgnoreCase(languageContext.getCurrentLanguage())) {
                        userService.updateUserLanguage(userId, languageContext.getCurrentLanguage());
                    } else {
                        languageContext.setCurrentLanguage(userLanguage);
                    }
                }
            } catch (Exception e) {
                // 토큰 파싱 실패 시 로그만 남기고 계속 진행
                logger.warn("Failed to parse token: {}", e.getMessage());
            }
        }
        return true;
    }
}
