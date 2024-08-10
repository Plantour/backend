package com.qnelldo.plantour.common.context;

import org.springframework.stereotype.Component;

@Component
public class LanguageContext {
    private static final ThreadLocal<String> currentLanguage = ThreadLocal.withInitial(() -> "ENG");

    public String getCurrentLanguage() {
        return currentLanguage.get();
    }

    public void setCurrentLanguage(String language) {
        currentLanguage.set(language);
    }

    public void clear() {
        currentLanguage.remove();
    }
}
