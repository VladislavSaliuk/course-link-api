package com.courselink.api.config;

import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Locale;

@Configuration
public class LanguageConfig {
    public static final List<Locale> LOCALES = List.of(
            new Locale("uk"),
            new Locale("en"),
            new Locale("ru"),
            new Locale("pl"),
            new Locale("de")
    );

}
