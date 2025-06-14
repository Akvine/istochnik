package ru.akvine.istochnik.services.impl.faker.barcode;

import net.datafaker.Faker;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.Topic;
import ru.akvine.istochnik.services.FakerGeneratorService;

@Service
public class Gtin8FakerGeneratorServiceImpl implements FakerGeneratorService {
    @Override
    public String generate(Faker faker) {
        return String.valueOf(faker.barcode().gtin8());
    }

    @Override
    public Topic getByTopic() {
        return Topic.GTIN8;
    }
}
