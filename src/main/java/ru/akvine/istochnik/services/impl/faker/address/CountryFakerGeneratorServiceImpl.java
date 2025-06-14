package ru.akvine.istochnik.services.impl.faker.address;

import net.datafaker.Faker;
import ru.akvine.istochnik.enums.Topic;
import ru.akvine.istochnik.services.FakerGeneratorService;

public class CountryFakerGeneratorServiceImpl implements FakerGeneratorService {
    @Override
    public String generate(Faker faker) {
        return faker.address().country();
    }

    @Override
    public Topic getByTopic() {
        return Topic.COUNTRY;
    }
}
