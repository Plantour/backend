package com.qnelldo.plantour.common.controller;

import com.qnelldo.plantour.common.context.LanguageContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LanguageController {

    private final LanguageContext languageContext;

    @Autowired
    public LanguageController(LanguageContext languageContext) {
        this.languageContext = languageContext;
    }

    @PostMapping("/api/language")
    public String setLanguage(@RequestParam("lang") String language) {
        languageContext.setCurrentLanguage(language);
        return "Language set to " + languageContext.getCurrentLanguage();
    }
}
