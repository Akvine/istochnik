package ru.akvine.istochnik.services.impl.faker.passport;

import net.datafaker.Faker;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.Topic;
import ru.akvine.istochnik.services.FakerGeneratorService;

@Service
public class RussianPassportFakerGeneratorServiceImpl implements FakerGeneratorService {
    @Override
    public String generate(Faker faker) {
        return faker.regexify("[0-9]{2} [0-9]{2} [0-9]{6}");
    }

    @Override
    public Topic getByTopic() {
        return Topic.RUSSIAN_PASSPORT;
    }
}
