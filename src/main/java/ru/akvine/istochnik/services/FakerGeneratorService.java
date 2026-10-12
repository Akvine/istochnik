package ru.akvine.istochnik.services;

import net.datafaker.Faker;
import ru.akvine.istochnik.enums.Topic;
import ru.akvine.istochnik.services.dto.Config;

public interface FakerGeneratorService {
    String generate(Faker faker);

    /**
     * Генерирует значение с учётом параметров колонки. Большинству топиков параметры не нужны
     */
    default String generate(Faker faker, Config config) {
        return generate(faker);
    }

    Topic getByTopic();
}
