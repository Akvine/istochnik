package ru.akvine.istochnik.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import ru.akvine.istochnik.enums.Language;
import ru.akvine.istochnik.services.MessageResolverService;
import ru.akvine.istochnik.services.impl.MessageResolverServiceImpl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Configuration
public class MessageResolverConfig {

    @Value("${default.encoding}")
    private String defaultEncoding;
    @Value("${localization.basename.messages.path}")
    private String localizationMessagesPath;

    @Bean
    public MessageResolverService messageResolveService() {

        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames(localizationMessagesPath);
        messageSource.setDefaultEncoding(defaultEncoding);

        Map<Language, Locale> locales = new HashMap<>();
        Language[] languages = Language.values();
        for (Language language : languages) {
            // TODO : уйти от Deprecated API
            locales.put(language, new Locale(language.name().toLowerCase()));
        }
        return new MessageResolverServiceImpl(messageSource, locales);
    }
}
