package ru.akvine.istochnik.services.converters.string.hash;

import java.util.List;
import java.util.random.RandomGenerator;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;
import ru.akvine.istochnik.services.converters.string.StringConverter;
import ru.akvine.istochnik.utils.CryptoUtils;

@Service
public class SHA256Converter extends StringConverter<String, String> {
    @Override
    public List<String> convert(
            List<String> input, String[] arguments, RandomGenerator randomGenerator, double probability) {
        return input.stream()
                .map(value -> randomGenerator.nextDouble() < probability ? CryptoUtils.hashSHA256(value) : value)
                .toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.SHA256;
    }
}
