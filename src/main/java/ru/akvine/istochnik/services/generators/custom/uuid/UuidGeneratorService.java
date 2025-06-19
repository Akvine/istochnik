package ru.akvine.istochnik.services.generators.custom.uuid;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.services.generators.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UuidGeneratorService {

    public List<String> generate(int size) {
        return generate(new Config(size, null));
    }

    public List<String> generate(Config config) {
        List<String> generatedValues = new ArrayList<>();

        for (int i = 0; i < config.getSize(); ++i) {
            generatedValues.add(UUID.randomUUID().toString());
        }

        return generatedValues;
    }

    public List<String> generate(Config config, String value) {
        List<String> generatedValues = new ArrayList<>();

        for (int i = 0; i < config.getSize(); ++i) {
            generatedValues.add(value);
        }

        return generatedValues;
    }
}
