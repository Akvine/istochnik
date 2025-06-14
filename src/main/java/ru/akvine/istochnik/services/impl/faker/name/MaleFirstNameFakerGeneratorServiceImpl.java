package ru.akvine.istochnik.services.impl.faker.name;

import net.datafaker.Faker;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.Topic;
import ru.akvine.istochnik.services.FakerGeneratorService;

@Service
public class MaleFirstNameFakerGeneratorServiceImpl implements FakerGeneratorService {
    @Override
    public String generate(Faker faker) {
        return faker.name().malefirstName();
    }

    @Override
    public Topic getByTopic() {
        return Topic.MALE_FIRST_NAME;
    }
}
