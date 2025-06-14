package ru.akvine.istochnik.services.impl.faker.barcode;

import net.datafaker.Faker;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.Topic;
import ru.akvine.istochnik.services.FakerGeneratorService;

@Service
public class Ean13FakerGeneratorServiceImpl implements FakerGeneratorService {
    @Override
    public String generate(Faker faker) {
        return String.valueOf(faker.barcode().ean13());
    }

    @Override
    public Topic getByTopic() {
        return Topic.EAN13;
    }
}
