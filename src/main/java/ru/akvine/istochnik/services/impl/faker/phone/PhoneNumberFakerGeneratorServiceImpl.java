package ru.akvine.istochnik.services.impl.faker.phone;

import net.datafaker.Faker;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.Topic;
import ru.akvine.istochnik.services.FakerGeneratorService;

@Service
public class PhoneNumberFakerGeneratorServiceImpl implements FakerGeneratorService {
    @Override
    public String generate(Faker faker) {
        return faker.phoneNumber().phoneNumber();
    }

    @Override
    public Topic getByTopic() {
        return Topic.PHONE_NUMBER;
    }
}
