package ru.akvine.istochnik.services;

import ru.akvine.istochnik.enums.Language;

public interface MessageResolverService {
    String message(String code);

    String message(String code, Language language);

    String message(String code, Language language, Object... params);
}
