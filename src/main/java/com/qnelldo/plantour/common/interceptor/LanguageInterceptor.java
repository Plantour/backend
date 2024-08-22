package com.qnelldo.plantour.common.interceptor;

import com.qnelldo.plantour.auth.service.JwtTokenProvider;
import com.qnelldo.plantour.common.context.LanguageContext;
import com.qnelldo.plantour.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;

@Component
public class LanguageInterceptor implements HandlerInterceptor {
    private final LanguageContext languageContext;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public LanguageInterceptor(LanguageContext languageContext, JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.languageContext = languageContext;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String languageHeader = request.getHeader("X-User-Language");
        String token = request.getHeader("Authorization");

        // 유효한 언어 코드 검증 (KOR 또는 ENG만 허용)
        if (languageHeader != null && (languageHeader.equalsIgnoreCase("KOR") || languageHeader.equalsIgnoreCase("ENG"))) {
            languageContext.setCurrentLanguage(languageHeader.toUpperCase());

        } else {
            languageContext.setCurrentLanguage("ENG");  // 기본값 설정
        }

        // 로그인된 사용자의 경우 언어 설정을 DB에 저장
        if (token != null && token.startsWith("Bearer ")) {
            Long userId = jwtTokenProvider.extractUserIdFromAuthorizationHeader(token);

            if (userId != null) {
                String userLanguage = userService.getUserLanguage(userId);

                if (!userLanguage.equalsIgnoreCase(languageContext.getCurrentLanguage())) {
                    userService.updateUserLanguage(userId, languageContext.getCurrentLanguage());

                } else {
                    languageContext.setCurrentLanguage(userLanguage);
                }
            }
        }
        return true;
    }
}
