package ru.akvine.istochnik.services.impl.faker.barcode;

import net.datafaker.Faker;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.Topic;
import ru.akvine.istochnik.services.FakerGeneratorService;

@Service
public class Ean8FakerGeneratorServiceImpl implements FakerGeneratorService {
    @Override
    public String generate(Faker faker) {
        return String.valueOf(faker.barcode().ean8());
    }

    @Override
    public Topic getByTopic() {
        return Topic.EAN8;
    }
}
