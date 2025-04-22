package ru.akvine.istochnik.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import ru.akvine.istochnik.enums.Language;
import ru.akvine.istochnik.services.MessageResolverService;

import java.util.Locale;
import java.util.Map;

@RequiredArgsConstructor
public class MessageResolverServiceImpl implements MessageResolverService {
    private final MessageSource messageSource;
    private final Map<Language, Locale> locales;

    @Override
    public String message(String code) {
        return message(code, Language.EN);
    }

    @Override
    public String message(String code, Language language) {
        return message(code, language, (Object) null);
    }

    @Override
    public String message(String code, Language language, Object... params) {
        return messageSource.getMessage(code, params, code, locales.get(language));
    }
}
