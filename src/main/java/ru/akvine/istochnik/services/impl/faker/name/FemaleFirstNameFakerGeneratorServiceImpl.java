package ru.akvine.istochnik.services.impl.faker.name;

import net.datafaker.Faker;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.Topic;
import ru.akvine.istochnik.services.FakerGeneratorService;

@Service
public class FemaleFirstNameFakerGeneratorServiceImpl implements FakerGeneratorService {
    @Override
    public String generate(Faker faker) {
        return faker.name().femaleFirstName();
    }

    @Override
    public Topic getByTopic() {
        return Topic.FEMALE_FIRST_NAME;
    }
}
