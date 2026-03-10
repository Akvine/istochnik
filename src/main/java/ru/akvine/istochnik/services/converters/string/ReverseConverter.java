package ru.akvine.istochnik.services.converters.string;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.List;
import java.util.random.RandomGenerator;

@Service
public class ReverseConverter extends StringConverter<String, String> {
    @Override
    public List<String> convert(List<String> input,
                                String[] arguments,
                                RandomGenerator randomGenerator,
                                double probability) {
        return input.stream().map(value -> randomGenerator.nextDouble() < probability ?
                        StringUtils.reverse(value) : value)
                .toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.REVERSE;
    }
}
