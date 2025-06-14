package ru.akvine.istochnik.services.impl.faker.name;

import net.datafaker.Faker;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.Topic;
import ru.akvine.istochnik.services.FakerGeneratorService;

@Service
public class FullNameFakerGeneratorServiceImpl implements FakerGeneratorService {
    @Override
    public String generate(Faker faker) {
        return faker.name().fullName();
    }

    @Override
    public Topic getByTopic() {
        return Topic.FULL_NAME;
    }
}
