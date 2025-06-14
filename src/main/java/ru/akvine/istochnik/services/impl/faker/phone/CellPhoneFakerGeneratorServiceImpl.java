package ru.akvine.istochnik.services.impl.faker.phone;

import net.datafaker.Faker;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.Topic;
import ru.akvine.istochnik.services.FakerGeneratorService;

@Service
public class CellPhoneFakerGeneratorServiceImpl implements FakerGeneratorService {
    @Override
    public String generate(Faker faker) {
        return faker.phoneNumber().cellPhone();
    }

    @Override
    public Topic getByTopic() {
        return Topic.CELL_PHONE;
    }
}
