package ru.akvine.istochnik.services.impl.faker.address;

import net.datafaker.Faker;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.Topic;
import ru.akvine.istochnik.services.FakerGeneratorService;

@Service
public class ZipCodeFakerGeneratorServiceImpl implements FakerGeneratorService {
    @Override
    public String generate(Faker faker) {
        return faker.address().zipCode();
    }

    @Override
    public Topic getByTopic() {
        return Topic.ZIP_CODE;
    }
}
