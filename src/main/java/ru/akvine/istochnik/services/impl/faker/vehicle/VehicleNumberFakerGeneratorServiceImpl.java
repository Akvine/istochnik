package ru.akvine.istochnik.services.impl.faker.vehicle;

import net.datafaker.Faker;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.Topic;
import ru.akvine.istochnik.services.FakerGeneratorService;

@Service
public class VehicleNumberFakerGeneratorServiceImpl implements FakerGeneratorService {
    @Override
    public String generate(Faker faker) {
        return faker.vehicle().licensePlate();
    }

    @Override
    public Topic getByTopic() {
        return Topic.VEHICLE_NUMBER;
    }
}
