package ru.akvine.istochnik.services.impl.faker.passport.international;

import net.datafaker.Faker;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.Topic;
import ru.akvine.istochnik.services.FakerGeneratorService;

@Service
public class InternationalCommonPassportFakerGeneratorServiceImpl implements FakerGeneratorService {
    @Override
    public String generate(Faker faker) {
        return faker.regexify("[A-Z]{2}[0-9]{7}");
    }

    @Override
    public Topic getByTopic() {
        return Topic.INTERNATIONAL_COMMON_PASSPORT;
    }
}
