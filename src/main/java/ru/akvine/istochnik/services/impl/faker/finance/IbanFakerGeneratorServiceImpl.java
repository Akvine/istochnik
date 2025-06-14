package ru.akvine.istochnik.services.impl.faker.finance;

import net.datafaker.Faker;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.Topic;
import ru.akvine.istochnik.services.FakerGeneratorService;

@Service
public class IbanFakerGeneratorServiceImpl implements FakerGeneratorService {
    @Override
    public String generate(Faker faker) {
        return faker.finance().iban();
    }

    @Override
    public Topic getByTopic() {
        return Topic.IBAN;
    }
}
