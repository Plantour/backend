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
        String acceptLanguage = request.getHeader("X-User-Language");
        String token = request.getHeader("Authorization");

        // Default language
        if (token != null && token.startsWith("Bearer ")) {
            Long userId = jwtTokenProvider.extractUserIdFromAuthorizationHeader(token);
            String userLanguage = userService.getUserLanguage(userId);
            languageContext.setCurrentLanguage(userLanguage);
        } else languageContext.setCurrentLanguage(Objects.requireNonNullElse(acceptLanguage, "ENG"));

        return true;
    }
}