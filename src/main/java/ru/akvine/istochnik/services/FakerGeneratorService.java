package ru.akvine.istochnik.services;

import net.datafaker.Faker;
import ru.akvine.istochnik.enums.Topic;

public interface FakerGeneratorService {
    String generate(Faker faker);

    Topic getByTopic();
}
