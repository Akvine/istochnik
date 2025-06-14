package ru.akvine.istochnik.services.impl.faker.internet;

import net.datafaker.Faker;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.Topic;
import ru.akvine.istochnik.services.FakerGeneratorService;

@Service
public class IPv4FakerGeneratorServiceImpl implements FakerGeneratorService {
    @Override
    public String generate(Faker faker) {
        return faker.internet().ipV4Address();
    }

    @Override
    public Topic getByTopic() {
        return Topic.IPV4;
    }
}
